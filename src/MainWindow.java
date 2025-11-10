import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;


public class MainWindow {
    // ===== 固定参数 =====
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 700;

    private static final int CELL_W = 30;   // 单元格宽
    private static final int CELL_H = 30;   // 单元格高
    private static final int HGAP = 5;      // 水平间距
    private static final int VGAP = 5;      // 垂直间距

    private int COLS;           // 列数
    private int COLS_PER_ROW;   // 每行数量
    private int TOTAL_COUNT;    // 按钮总数

    public MainWindow(int difficulty) {
        switch (difficulty) {
            case 1:
                COLS = 5;
                COLS_PER_ROW = 5;
                TOTAL_COUNT = 25;
                break;
            case 2:
                COLS = 6;
                COLS_PER_ROW = 6;
                TOTAL_COUNT = 36;
                break;
            case 3:
                COLS = 7;
                COLS_PER_ROW = 7;
                TOTAL_COUNT = 49;
                break;
            case 4:
                COLS = 8;
                COLS_PER_ROW = 8;
                TOTAL_COUNT = 64;
                break;
            default:
                COLS = 5;
                COLS_PER_ROW = 5;
                TOTAL_COUNT = 25;
        }
    }

    public void setVisible(boolean visible) {
        if (visible) {
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("CubeVerse");
                frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.setLayout(null);

                // 背景面板
                MainWindowBack bgPanel = new MainWindowBack("/MainWindowBack.png");
                bgPanel.setLayout(null);
                bgPanel.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
                frame.setContentPane(bgPanel);

                // 管理器
                SwapManager manager = new SwapManager(300);

                // 自动居中创建按钮
                createButtons(bgPanel, manager, TOTAL_COUNT);
                // 新增：添加返回主菜单按钮 + ESC 快捷键
                addBackToMenu(bgPanel, frame);

                frame.setVisible(true);
            });
        }
    }

    private void createButtons(JPanel bgPanel, SwapManager manager, int totalCount) {
        String[] imagePaths = {
                "/Block_001.png", "/Block_002.png", "/Block_003.png", "/Block_004.png", "/Block_005.png",
                "/Block_006.png", "/Block_007.png", "/Block_008.png", "/Block_009.png", "/Block_010.png"
        };

        List<ImageButton> buttons = new ArrayList<>();

        // ---- 水平居中 ----
        int totalWidth = COLS_PER_ROW * (CELL_W + HGAP) - HGAP;
        int ORIGIN_X = (WINDOW_WIDTH - totalWidth) / 2;

        // ---- 垂直居中 ----
        int rows = (int) Math.ceil((double) totalCount / COLS_PER_ROW);
        int totalHeight = rows * (CELL_H + VGAP) - VGAP;
        int ORIGIN_Y = (WINDOW_HEIGHT - totalHeight) / 2;

        for (int i = 0; i < totalCount; i++) {
            int row = i / COLS_PER_ROW;
            int col = i % COLS_PER_ROW;

            int x = ORIGIN_X + col * (CELL_W + HGAP);
            int y = ORIGIN_Y + row * (CELL_H + VGAP);

            String path = imagePaths[i % imagePaths.length];
            ImageButton btn = createImageButton(path, x, y, ORIGIN_X, ORIGIN_Y, manager);
            buttons.add(btn);
            bgPanel.add(btn);
        }

        bgPanel.revalidate();
        bgPanel.repaint();
    }

    private void removeAllActionListeners(AbstractButton b) {
        for (ActionListener al : b.getActionListeners()) {
            b.removeActionListener(al);
        }
    }

    private ImageButton createImageButton(String path, int x, int y,
                                          int originX, int originY,
                                          SwapManager manager) {
        ImageButton btn = new ImageButton(path, CELL_W, CELL_H);
        btn.setLocation(x, y);

        // 使用统一的网格原点，而不是当前按钮坐标 (x, y)
        btn.configureGrid(CELL_W, CELL_H, originX, originY, COLS, HGAP, VGAP);

        btn.snapToGrid();
        removeAllActionListeners(btn);
        btn.addActionListener(manager);
        return btn;
    }


    private void addBackToMenu(JPanel parent, JFrame frame) {
        JButton back = new JButton("Back to Menu");
        back.setFocusPainted(false);
        back.setBounds(20, 20, 140, 32); // 放在左上角，你可改位置/尺寸
        back.addActionListener(e -> {
            frame.dispose();
            // 返回主菜单
            SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
        });
        parent.add(back);

        // 绑定 ESC 快捷键：按下即返回主菜单
        JRootPane root = frame.getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goBack");
        root.getActionMap().put("goBack", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                frame.dispose();
                SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
            }
        });
    }
}
