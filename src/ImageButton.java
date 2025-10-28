import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.URL;

public class ImageButton extends JButton {
    private boolean ready = false;
    private boolean animating = false; // 防止动画中再次交换

    public ImageButton(String resourcePath, int width, int height) {
        // 加载图像资源
        URL url = getClass().getResource(resourcePath);
        if (url == null) {
            System.err.println("⚠️ 找不到图片：" + resourcePath);
            return;
        }
        ImageIcon icon = new ImageIcon(url);
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(scaled));

        // 去掉默认样式
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);

        // 手动布局时的尺寸
        setSize(width, height);
    }

    /** 设置高亮状态 */
    public void setReady(boolean ready) {
        this.ready = ready;
        setBorderPainted(ready);
        setBorder(ready ? new LineBorder(Color.YELLOW, 3, true) : null);
        repaint();
    }

    public boolean isReady() {
        return ready;
    }

    /** 按指定时长平滑交换两个按钮的位置 */
    public void swapWith(ImageButton other, int durationMs) {
        if (other == null || animating || other.animating) return;

        Container parent = getParent();
        if (parent == null || parent != other.getParent()) return;

        animating = true;
        other.animating = true;

        // 起始位置
        Point a0 = this.getLocation();
        Point b0 = other.getLocation();

        int steps = Math.max(1, durationMs / 15); // 每帧约15ms
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
                this.setLocation(b0);
                other.setLocation(a0);
                ((Timer) e.getSource()).stop();
                animating = false;
                other.animating = false;
            }
        });
        timer.start();
    }
}
