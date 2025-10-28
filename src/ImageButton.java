import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.URL;

/**
 * 支持网格感知与“仅上下左右相邻可交换”的 ImageButton。
 * 用法：
 * 1) 创建按钮后，调用 configureGrid(cellW, cellH, originX, originY, cols) 或带间距的重载方法；
 * 2) 调用 swapWith(other, durationMs) 时，内部会先判断是否相邻，不相邻则不会移动并发出 beep。
 */
public class ImageButton extends JButton {
    private boolean ready = false;
    private boolean animating = false; // 防止动画中再次交换

    // ===== 网格参数（启用后才能判断相邻） =====
    private boolean gridEnabled = false;
    private int cellW, cellH;         // 单元格宽高
    private int originX, originY;     // 网格起点（左上角像素）
    private int cols;                 // 列数（用于从索引还原位置时换算）
    private int hgap = 0, vgap = 0;   // 水平/垂直间距（可选）

    private String typeKey;  // 用于三消判定的“类型”

    public ImageButton(String resourcePath, int width, int height) {
        // ===== 1️⃣ 保存图片类型（用路径名作为标识即可）=====
        this.typeKey = resourcePath;   // 用于 Match3 判定的类型识别

        // ===== 2️⃣ 加载图像资源 =====
        URL url = getClass().getResource(resourcePath);
        if (url == null) {
            System.err.println("⚠️ 找不到图片：" + resourcePath);
        } else {
            ImageIcon icon = new ImageIcon(url);
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(scaled));
        }

        // ===== 3️⃣ 去掉默认样式 =====
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);

        // ===== 4️⃣ 手动布局时的尺寸 =====
        setSize(width, height);
    }


    public String getType() { return typeKey; }

    /* -------------------- 网格配置 -------------------- */

    /** 启用网格：无间距版本 */
    public void configureGrid(int cellW, int cellH, int originX, int originY, int cols) {
        configureGrid(cellW, cellH, originX, originY, cols, 0, 0);
    }

    /** 启用网格：带间距版本（若网格格子之间有空隙，用这个） */
    public void configureGrid(int cellW, int cellH, int originX, int originY, int cols, int hgap, int vgap) {
        if (cellW <= 0 || cellH <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Grid params invalid.");
        }
        this.cellW = cellW;
        this.cellH = cellH;
        this.originX = originX;
        this.originY = originY;
        this.cols = cols;
        this.hgap = Math.max(0, hgap);
        this.vgap = Math.max(0, vgap);
        this.gridEnabled = true;
    }

    /* -------------------- 高亮状态 -------------------- */

    /** 设置高亮状态（第一次点击的“准备”高亮） */
    public void setReady(boolean ready) {
        this.ready = ready;
        setBorderPainted(ready);
        setBorder(ready ? new LineBorder(Color.YELLOW, 3, true) : null);
        repaint();
    }

    public boolean isReady() {
        return ready;
    }

    /* -------------------- 交换逻辑（含相邻判断与吸附） -------------------- */

    /** 按指定时长平滑交换两个按钮的位置；仅允许上下左右相邻（曼哈顿距离 1）。 */
    public void swapWith(ImageButton other, int durationMs) {
        // 兼容旧写法：默认没有回调
        swapWith(other, durationMs, null);
    }

    /** 平滑交换两个按钮，并在动画完成后执行回调（例如触发三消判定）。 */
    public void swapWith(ImageButton other, int durationMs, Runnable onComplete) {
        if (other == null || animating || other.animating) return;

        Container parent = getParent();
        if (parent == null || parent != other.getParent()) return;

        // 若未配置网格，直接使用像素交换（可选）
        if (!gridEnabled || !other.gridEnabled) {
            pixelSwapWith(other, durationMs);
            if (onComplete != null) SwingUtilities.invokeLater(onComplete);
            return;
        }

        // 计算各自 (row, col)
        Point rcA = rcOf(getLocation());
        Point rcB = rcOf(other.getLocation());

        // 仅允许上下左右相邻
        if (!areNeighbors(rcA, rcB)) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        // 目标格像素坐标
        Point targetA = locationOfCell(rcB.x, rcB.y);
        Point targetB = locationOfCell(rcA.x, rcA.y);

        animating = true;
        other.animating = true;

        Point a0 = this.getLocation();
        Point b0 = other.getLocation();

        int steps = Math.max(1, Math.max(1, durationMs) / 15);
        int delay = Math.max(1, durationMs / steps);

        double dax = (targetA.x - a0.x) / (double) steps;
        double day = (targetA.y - a0.y) / (double) steps;
        double dbx = (targetB.x - b0.x) / (double) steps;
        double dby = (targetB.y - b0.y) / (double) steps;

        double[] ax = {a0.x}, ay = {a0.y};
        double[] bx = {b0.x}, by = {b0.y};
        final int[] t = {0};

        Timer timer = new Timer(delay, e -> {
            t[0]++;
            ax[0] += dax; ay[0] += day;
            bx[0] += dbx; by[0] += dby;

            this.setLocation((int) Math.round(ax[0]), (int) Math.round(ay[0]));
            other.setLocation((int) Math.round(bx[0]), (int) Math.round(by[0]));

            if (t[0] >= steps) {
                ((Timer) e.getSource()).stop();
                // 精确吸附格子
                this.setLocation(targetA);
                other.setLocation(targetB);
                animating = false;
                other.animating = false;

                // ✅ 动画完成后执行回调（例如触发 Match3 消除）
                if (onComplete != null) onComplete.run();
            }
        });
        timer.start();
    }


    /** 原始像素交换（不判断相邻）；仅在未启用网格时兜底使用。 */
    private void pixelSwapWith(ImageButton other, int durationMs) {
        animating = true;
        other.animating = true;

        Point a0 = this.getLocation();
        Point b0 = other.getLocation();

        int steps = Math.max(1, Math.max(1, durationMs) / 15);
        int delay = Math.max(1, durationMs / steps);

        double dax = (b0.x - a0.x) / (double) steps;
        double day = (b0.y - a0.y) / (double) steps;
        double dbx = (a0.x - b0.x) / (double) steps;
        double dby = (a0.y - b0.y) / (double) steps;

        double[] ax = {a0.x}, ay = {a0.y};
        double[] bx = {b0.x}, by = {b0.y};
        final int[] t = {0};

        Timer timer = new Timer(delay, e -> {
            t[0]++;
            ax[0] += dax; ay[0] += day;
            bx[0] += dbx; by[0] += dby;
            this.setLocation((int) Math.round(ax[0]), (int) Math.round(ay[0]));
            other.setLocation((int) Math.round(bx[0]), (int) Math.round(by[0]));

            if (t[0] >= steps) {
                ((Timer) e.getSource()).stop();
                this.setLocation(b0);
                other.setLocation(a0);
                animating = false;
                other.animating = false;
            }
        });
        timer.start();
    }

    /* -------------------- 网格换算工具 -------------------- */

    /** 将像素位置换算为 (row, col)；考虑起点与间距。 */
    private Point rcOf(Point p) {
        // 相对网格起点的偏移
        double dx = p.x - originX;
        double dy = p.y - originY;

        // 单元实际步进 = cell 尺寸 + gap
        int stepX = cellW + hgap;
        int stepY = cellH + vgap;

        // 用四舍五入保证一定容错（按钮在格内稍有偏移也能识别）
        int col = (int) Math.round(dx / stepX);
        int row = (int) Math.round(dy / stepY);

        return new Point(row, col);
    }

    /** 给定 (row, col) 返回该格左上角像素位置；考虑起点与间距。 */
    private Point locationOfCell(int row, int col) {
        int x = originX + col * (cellW + hgap);
        int y = originY + row * (cellH + vgap);
        return new Point(x, y);
    }

    /** 是否上下左右相邻（曼哈顿距离 1） */
    private static boolean areNeighbors(Point rcA, Point rcB) {
        int dr = Math.abs(rcA.x - rcB.x);
        int dc = Math.abs(rcA.y - rcB.y);
        return (dr + dc) == 1;
    }

    /** 将按钮吸附到其“所在格”的精确位置（可在拖拽或初始化后调用）。 */
    public void snapToGrid() {
        if (!gridEnabled) return;
        Point rc = rcOf(getLocation());
        setLocation(locationOfCell(rc.x, rc.y));
    }

    /* -------------------- 便捷访问 -------------------- */

    /** 返回当前所在的行（需先 configureGrid） */
    public int currentRow() { return rcOf(getLocation()).x; }
    /** 返回当前所在的列（需先 configureGrid） */
    public int currentCol() { return rcOf(getLocation()).y; }

    public boolean isAnimating() { return animating; }
    public void setAnimating(boolean a) { this.animating = a; }
}
