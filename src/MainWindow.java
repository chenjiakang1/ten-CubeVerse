import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
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

    private int ORIGIN_X;
    private int ORIGIN_Y;

    public MainWindow(int difficulty) {
        switch (difficulty) {
            case 1:
                COLS = 5; COLS_PER_ROW = 5; TOTAL_COUNT = 25; break;
            case 2:
                COLS = 6; COLS_PER_ROW = 6; TOTAL_COUNT = 36; break;
            case 3:
                COLS = 7; COLS_PER_ROW = 7; TOTAL_COUNT = 49; break;
            case 4:
                COLS = 8; COLS_PER_ROW = 8; TOTAL_COUNT = 64; break;
            default:
                COLS = 5; COLS_PER_ROW = 5; TOTAL_COUNT = 25;
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
                SwapManager manager = new SwapManager(this, 300);

                // 自动居中创建按钮（随机 & 不含任何“可消连通块”）
                createButtons(bgPanel, manager, TOTAL_COUNT);

                // 返回主菜单按钮 + ESC
                addBackToMenu(bgPanel, frame);

                frame.setVisible(true);
            });
        }
    }

    /** 生成整盘：用 4 邻接连通块检测避免“初始即消” */
    private void createButtons(JPanel bgPanel, SwapManager manager, int totalCount) {
        String[] imagePaths = {
                "/Block_001.png", "/Block_002.png", "/Block_003.png", "/Block_004.png", "/Block_005.png",
                "/Block_006.png", "/Block_007.png", "/Block_008.png", "/Block_009.png", "/Block_010.png"
        };
        Random random = new Random();

        // ---- 居中计算 ----
        int totalWidth  = COLS_PER_ROW * (CELL_W + HGAP) - HGAP;
        this.ORIGIN_X = (WINDOW_WIDTH  - totalWidth)  / 2;
        int rows        = (int) Math.ceil((double) totalCount / COLS_PER_ROW);
        int totalHeight = rows * (CELL_H + VGAP) - VGAP;
        this.ORIGIN_Y = (WINDOW_HEIGHT - totalHeight) / 2;

        // 反复随机直到“没有任何可消连通块”
        final int MAX_ATTEMPTS = 2000;
        int[][] types = null;
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            types = randomFill(rows, COLS_PER_ROW, imagePaths.length, random);
            if (!hasAnyMatchByFourAdj(types, rows, COLS_PER_ROW)) {
                break; // 合格
            }
            if (attempt == MAX_ATTEMPTS - 1) {
                System.err.println("⚠️ 初始盘面生成达到上限，仍存在可消块：将使用最后一次结果（可能会开局即消）。");
            }
        }

        // 落盘
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < COLS_PER_ROW; c++) {
                int x = ORIGIN_X + c * (CELL_W + HGAP);
                int y = ORIGIN_Y + r * (CELL_H + VGAP);
                String path = imagePaths[types[r][c]];
                ImageButton btn = createImageButton(path, x, y, ORIGIN_X, ORIGIN_Y, manager);
                bgPanel.add(btn);
            }
        }

        bgPanel.revalidate();
        bgPanel.repaint();
    }

    /** 用随机数填充整盘（纯随机即可；可加点局部约束提升成功率） */
    private int[][] randomFill(int rows, int cols, int kindCount, Random rnd) {
        int[][] g = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                g[r][c] = rnd.nextInt(kindCount);
            }
        }
        return g;
    }

    /** 是否存在“4 邻接连通块大小 ≥3”（与 Match3Manager 规则一致） */
    private boolean hasAnyMatchByFourAdj(int[][] g, int rows, int cols) {
        boolean[][] vis = new boolean[rows][cols];
        int[][] DIRS = { {1,0}, {-1,0}, {0,1}, {0,-1} };

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (vis[r][c]) continue;
                int t = g[r][c];
                Queue<int[]> q = new ArrayDeque<>();
                q.add(new int[]{r,c});
                vis[r][c] = true;
                int size = 0;

                while (!q.isEmpty()) {
                    int[] cur = q.poll();
                    size++;
                    for (int[] d : DIRS) {
                        int nr = cur[0] + d[0], nc = cur[1] + d[1];
                        if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                        if (vis[nr][nc]) continue;
                        if (g[nr][nc] != t) continue;
                        vis[nr][nc] = true;
                        q.add(new int[]{nr,nc});
                    }
                }
                if (size >= 3) return true; // 发现可消块
            }
        }
        return false;
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
        btn.configureGrid(CELL_W, CELL_H, originX, originY, COLS, HGAP, VGAP);
        btn.snapToGrid();
        removeAllActionListeners(btn);
        btn.addActionListener(manager);
        return btn;
    }

    private void addBackToMenu(JPanel parent, JFrame frame) {
        JButton back = new JButton("Back to Menu");
        back.setHorizontalTextPosition(SwingConstants.CENTER);
        back.setVerticalTextPosition(SwingConstants.CENTER);
        back.setFont(new Font("Arial", Font.BOLD, 14));
        back.setForeground(Color.WHITE);

        java.net.URL imgURL = getClass().getResource("/button.png");
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image scaled = icon.getImage().getScaledInstance(140, 40, Image.SCALE_SMOOTH);
            back.setIcon(new ImageIcon(scaled));
        } else {
            System.err.println("⚠️ 找不到 /button.png");
        }

        back.setBorderPainted(false);
        back.setContentAreaFilled(false);
        back.setFocusPainted(false);
        back.setOpaque(false);

        back.setBounds(20, 20, 140, 40);
        back.addActionListener(e -> {
            frame.dispose();
            SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
        });

        parent.add(back);

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

    /** 菜单按钮（独立于棋盘） */
    private JButton createImageButton(String text) {
        JButton btn = new JButton(text);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.CENTER);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);

        java.net.URL imgURL = getClass().getResource("/button.png");
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image scaled = icon.getImage().getScaledInstance(240, 60, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(scaled));
        }

        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(240, 60));
        btn.setMaximumSize(new Dimension(240, 60));

        return btn;
    }

    public int getCellW() { return CELL_W; }
    public int getCellH() { return CELL_H; }
    public int getHGap() { return HGAP; }
    public int getVGap() { return VGAP; }
    public int getCols() { return COLS; }
    public int getOriginX() { return ORIGIN_X; }
    public int getOriginY() { return ORIGIN_Y; }
}
