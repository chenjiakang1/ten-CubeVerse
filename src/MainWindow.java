import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MainWindow {
    // ===== 网格参数（务必与摆放像素匹配） =====
    private static final int ORIGIN_X = 60;   // 网格左上角 X
    private static final int ORIGIN_Y = 100;  // 网格左上角 Y
    private static final int CELL_W   = 30;   // 单元格宽
    private static final int CELL_H   = 30;   // 单元格高
    private static final int HGAP     = 5;    // 水平间距
    private static final int VGAP     = 5;    // 垂直间距
    private static final int COLS     = 8;    // 列数（>= 实际列数即可）
    private static final int COLS_PER_ROW = 8; // 每行放 8 个

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("CubeVerse");
            frame.setSize(400, 700);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setLayout(null);

            // 背景面板
            MainWindowBack bgPanel = new MainWindowBack("/MainWindowBack.png");
            bgPanel.setLayout(null);
            bgPanel.setBounds(0, 0, 400, 700);
            frame.setContentPane(bgPanel);

            // 统一的 SwapManager（动画时长 300ms）
            SwapManager manager = new SwapManager(300);

            // ✅ 调用我们封装的方法，一次生成 20 个按钮（你可以改数量）
            createButtons(bgPanel, manager, 40);

            frame.setVisible(true);
        });
    }

    /**
     * 封装方法：在背景面板上批量创建按钮
     * 图片不够时自动循环使用 /Block_001.png ~ /Block_010.png
     *
     * @param bgPanel 背景面板
     * @param manager SwapManager 实例
     * @param totalCount 要生成的按钮数量
     */
    private static void createButtons(JPanel bgPanel, SwapManager manager, int totalCount) {
        // 预定义图片资源路径数组（10 张）
        String[] imagePaths = {
                "/Block_001.png", "/Block_002.png", "/Block_003.png", "/Block_004.png", "/Block_005.png",
                "/Block_006.png", "/Block_007.png", "/Block_008.png", "/Block_009.png", "/Block_010.png"
        };

        List<ImageButton> buttons = new ArrayList<>();

        for (int i = 0; i < totalCount; i++) {
            int row = i / COLS_PER_ROW;
            int col = i % COLS_PER_ROW;

            int x = ORIGIN_X + col * (CELL_W + HGAP);
            int y = ORIGIN_Y + row * (CELL_H + VGAP);

            // 自动循环使用图片
            String path = imagePaths[i % imagePaths.length];

            ImageButton btn = createImageButton(path, x, y, manager);
            buttons.add(btn);
            bgPanel.add(btn);
        }

        bgPanel.revalidate();
        bgPanel.repaint();
    }

    private static void removeAllActionListeners(AbstractButton b) {
        for (ActionListener al : b.getActionListeners()) {
            b.removeActionListener(al);
        }
    }

    /** 工具方法：创建带图片、位置、监听、并绑定网格的按钮 */
    private static ImageButton createImageButton(String path, int x, int y, SwapManager manager) {
        ImageButton btn = new ImageButton(path, CELL_W, CELL_H);

        // 绝对布局摆放
        btn.setLocation(x, y);

        // 绑定网格参数
        btn.configureGrid(CELL_W, CELL_H, ORIGIN_X, ORIGIN_Y, COLS, HGAP, VGAP);

        // 吸附格子
        btn.snapToGrid();

        // 绑定监听
        removeAllActionListeners(btn);
        btn.addActionListener(manager);
        return btn;
    }
}
