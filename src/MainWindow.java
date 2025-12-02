package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

public class MainWindow {
    // ========== 记分 & 通关字段 ==========
    private JLabel scoreLabel;          // 显示右上角分数
    private int goalScore;              // 该局目标分数
    private boolean levelCleared;       // 防止重复触发通关

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

    private int stepsLeft;      // 剩余步数
    private JLabel stepsLabel;  // 显示步数

    private String mode;        // 难度

    // ========== 道具模式 & 次数 ==========
    private enum GameMode {
        NORMAL,
        BOMB,
        CLEAR_SAME
    }

    private GameMode currentMode = GameMode.NORMAL;

    // 道具次数（你可以根据需要改初始值）
    private int bombCount = 3;
    private int shuffleCount = 2;
    private int clearCount = 2;

    // 道具按钮 & 显示次数的 Label
    private JButton bombBtn;
    private JButton shuffleBtn;
    private JButton clearBtn;

    private JLabel bombCountLabel;
    private JLabel shuffleCountLabel;
    private JLabel clearCountLabel;


    public MainWindow(int difficulty) {
        // 新建 MainWindow = 开始一局 ⇒ 分数清零
        ScoreManager.reset();

        switch (difficulty) {
            case 1:
                COLS = 5; COLS_PER_ROW = 5; TOTAL_COUNT = 25;
                goalScore = 100;   // 简单模式通关分
                stepsLeft = 30;    // 简单模式步数（你原来这里写的是 3，我改回 30 更合理）
                mode = "easy";
                break;
            case 2:
                COLS = 6; COLS_PER_ROW = 6; TOTAL_COUNT = 36;
                goalScore = 200;
                stepsLeft = 60;
                mode = "normal";
                break;
            case 3:
                COLS = 7; COLS_PER_ROW = 7; TOTAL_COUNT = 49;
                goalScore = 300;
                stepsLeft = 90;
                mode = "hard";
                break;
            case 4:
                COLS = 8; COLS_PER_ROW = 8; TOTAL_COUNT = 64;
                goalScore = 400;
                stepsLeft = 120;
                mode = "expert";
                break;
            default:
                COLS = 5; COLS_PER_ROW = 5; TOTAL_COUNT = 25;
                goalScore = 100;
                stepsLeft = 30;
                mode = "easy";
        }
    }

    public void setVisible(boolean visible) {
        if (!visible) return;

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("CubeVerse");
            frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setLayout(null);

            // 1. 背景层（最底层）
            MainWindowBack bgPanel = new MainWindowBack("/MainWindowBack.png");
            bgPanel.setLayout(null);
            bgPanel.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
            frame.setContentPane(bgPanel);

            // 2. 提示背景图（木牌）
            ImageIcon hintIcon = new ImageIcon(getClass().getResource("/HintPanel.png"));
            Image hintImg = hintIcon.getImage().getScaledInstance(180, 170, Image.SCALE_SMOOTH);
            JLabel hintBackground = new JLabel(new ImageIcon(hintImg));
            hintBackground.setBounds(WINDOW_WIDTH - 160, 0, 180, 170);
            bgPanel.add(hintBackground);

            // 3. 文字
            JLabel goalLabel = new JLabel("Goal: " + goalScore);
            goalLabel.setForeground(Color.CYAN);
            goalLabel.setFont(new Font("Arial", Font.BOLD, 12));
            goalLabel.setBounds(WINDOW_WIDTH - 120, 89, 130, 30);
            bgPanel.add(goalLabel);

            scoreLabel = new JLabel("Score: 0");
            scoreLabel.setForeground(Color.WHITE);
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 12));
            scoreLabel.setBounds(WINDOW_WIDTH - 120, 101, 130, 30);
            bgPanel.add(scoreLabel);
            ScoreManager.bindLabel(scoreLabel);

            JLabel coinLabel = new JLabel("Coins: " + CoinManager.getInstance().getCoins());
            coinLabel.setForeground(Color.YELLOW);
            coinLabel.setFont(new Font("Arial", Font.BOLD, 12));
            coinLabel.setBounds(WINDOW_WIDTH - 120, 113, 130, 30);
            bgPanel.add(coinLabel);

            new javax.swing.Timer(200, e ->
                    coinLabel.setText("Coins: " + CoinManager.getInstance().getCoins())
            ).start();

            stepsLabel = new JLabel("Steps: " + stepsLeft);
            stepsLabel.setForeground(Color.WHITE);
            stepsLabel.setFont(new Font("Arial", Font.BOLD, 12));
            stepsLabel.setBounds(WINDOW_WIDTH - 120, 125, 130, 30);
            bgPanel.add(stepsLabel);

            // 木牌放在底层
            int deepest = bgPanel.getComponentCount() - 1;
            bgPanel.setComponentZOrder(hintBackground, deepest);

            // 设定通关回调
            ScoreManager.setGoal(goalScore, finalScore -> {
                if (levelCleared) return;
                levelCleared = true;

                int coinsEarned = Math.max(1, finalScore / 50);
                CoinManager.getInstance().addCoins(coinsEarned);

                JFrame window = (JFrame) SwingUtilities.getWindowAncestor(scoreLabel);
                if (window != null) window.dispose();

                String msg = "<html>Stage Cleared!<br>"
                        + "Score: " + finalScore + " / " + goalScore + "<br>"
                        + "Coins earned: " + coinsEarned + "</html>";

                new EndWindow(msg).setVisible(true);
            });

            // === 创建棋盘 & 道具 ===
            SwapManager manager = new SwapManager(this, 300);
            createButtons(bgPanel, manager, TOTAL_COUNT);
            initPowerUps(bgPanel);   // ⭐ 新增：初始化道具
            addBackToMenu(bgPanel, frame);

            updatePowerUpUI();       // ⭐ 启动时刷新一次道具 UI
            frame.setVisible(true);
        });
    }

    /** 生成整盘：根据难度选择不同数量的图片 */
    private void createButtons(JPanel bgPanel, SwapManager manager, int totalCount) {

        // 图片资源
        String[] allImages = {
                "/Block_001.png", "/Block_002.png", "/Block_003.png", "/Block_004.png", "/Block_005.png",
                "/Block_006.png", "/Block_007.png", "/Block_008.png", "/Block_009.png", "/Block_010.png"
        };

        String[] imagePaths;

        // 根据难度决定图片数量
        switch (mode.toLowerCase()) {
            case "easy":
                imagePaths = Arrays.copyOfRange(allImages, 0, 5);
                break;

            case "normal":
                imagePaths = Arrays.copyOfRange(allImages, 0, 7);
                break;

            case "hard":
            case "expert":       // expert 使用全部 10 张
            default:
                imagePaths = allImages;
                break;
        }

        Random random = new Random();

        int totalWidth  = COLS_PER_ROW * (CELL_W + HGAP) - HGAP;
        this.ORIGIN_X = (WINDOW_WIDTH - totalWidth) / 2;

        int rows = (int) Math.ceil((double) totalCount / COLS_PER_ROW);
        int totalHeight = rows * (CELL_H + VGAP) - VGAP;
        this.ORIGIN_Y = (WINDOW_HEIGHT - totalHeight) / 2;

        final int MAX_ATTEMPTS = 2000;
        int[][] types = null;

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            types = randomFill(rows, COLS_PER_ROW, imagePaths.length, random);

            if (!hasAnyMatchByFourAdj(types, rows, COLS_PER_ROW)) {
                break;
            }

            if (attempt == MAX_ATTEMPTS - 1) {
                System.err.println("⚠ 初始盘面生成达到上限，仍存在可消块。");
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


    /** 用随机数填充整盘 */
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

    /** 棋盘格子按钮（根据当前模式选择：交换 / 炸弹 / 同色） */
    private ImageButton createImageButton(String path, int x, int y,
                                          int originX, int originY,
                                          SwapManager manager) {
        ImageButton btn = new ImageButton(path, CELL_W, CELL_H);
        btn.setLocation(x, y);
        btn.configureGrid(CELL_W, CELL_H, originX, originY, COLS, HGAP, VGAP);
        btn.snapToGrid();
        removeAllActionListeners(btn);

        btn.addActionListener(ev -> {
            ImageButton self = (ImageButton) ev.getSource();
            Container parent = self.getParent();
            if (!(parent instanceof JPanel)) return;
            JPanel panel = (JPanel) parent;

            switch (currentMode) {
                case NORMAL:
                    // 正常模式：走原来 SwapManager 的逻辑
                    manager.actionPerformed(ev);
                    break;

                case BOMB:
                    if (bombCount > 0) {
                        PowerUpManager.useBomb(panel, self);
                        bombCount--;
                        updatePowerUpUI();
                    }
                    currentMode = GameMode.NORMAL;
                    break;

                case CLEAR_SAME:
                    if (clearCount > 0) {
                        PowerUpManager.useClearSameType(panel, self);
                        clearCount--;
                        updatePowerUpUI();
                    }
                    currentMode = GameMode.NORMAL;
                    break;
            }
        });

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
            System.err.println(" 找不到 /button.png");
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

    /** 初始化三个道具按钮 + 次数标签 */
    private void initPowerUps(JPanel bgPanel) {
        int iconSize = 40;
        int iconGap = 10;

        int totalGridWidth = COLS_PER_ROW * (CELL_W + HGAP) - HGAP;
        int gridCenterX = ORIGIN_X + totalGridWidth / 2;

        int totalIconWidth = 3 * iconSize + 2 * iconGap;
        int firstIconX = gridCenterX - totalIconWidth / 2;
        int iconY = ORIGIN_Y - 60;

        // 💣 炸弹
        bombBtn = createIconButton("/power_bomb.png", firstIconX, iconY, iconSize, bgPanel);
        bombBtn.setToolTipText("Bomb: clear 3x3 area");
        bombBtn.addActionListener(e -> {
            if (bombCount <= 0) return;
            currentMode = GameMode.BOMB;
        });

        // 🔀 打乱
        shuffleBtn = createIconButton("/power_shuffle.png",
                firstIconX + iconSize + iconGap, iconY, iconSize, bgPanel);
        shuffleBtn.setToolTipText("Shuffle: randomize blocks");
        shuffleBtn.addActionListener(e -> {
            if (shuffleCount <= 0) return;
            PowerUpManager.useShuffle(bgPanel);
            shuffleCount--;
            updatePowerUpUI();
        });

        // 🎨 同色
        clearBtn = createIconButton("/power_clear.png",
                firstIconX + 2 * (iconSize + iconGap), iconY, iconSize, bgPanel);
        clearBtn.setToolTipText("Clear all blocks of one type");
        clearBtn.addActionListener(e -> {
            if (clearCount <= 0) return;
            currentMode = GameMode.CLEAR_SAME;
        });

        int labelY = iconY + iconSize + 2;

        bombCountLabel = new JLabel();
        bombCountLabel.setBounds(firstIconX + iconSize / 2 - 10, labelY, 40, 20);
        bombCountLabel.setForeground(Color.WHITE);
        bgPanel.add(bombCountLabel);

        shuffleCountLabel = new JLabel();
        shuffleCountLabel.setBounds(firstIconX + (iconSize + iconGap) + iconSize / 2 - 10, labelY, 40, 20);
        shuffleCountLabel.setForeground(Color.WHITE);
        bgPanel.add(shuffleCountLabel);

        clearCountLabel = new JLabel();
        clearCountLabel.setBounds(firstIconX + 2 * (iconSize + iconGap) + iconSize / 2 - 10, labelY, 40, 20);
        clearCountLabel.setForeground(Color.WHITE);
        bgPanel.add(clearCountLabel);
    }

    /** 创建带图标的 JButton（道具按钮用） */
    private JButton createIconButton(String path, int x, int y, int size, JComponent parent) {
        java.net.URL url = getClass().getResource(path);
        JButton btn;
        if (url == null) {
            System.err.println("图标资源未找到: " + path);
            btn = new JButton("?");
        } else {
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
            btn = new JButton(icon);
        }

        btn.setBounds(x, y, size, size);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);

        parent.add(btn);
        return btn;
    }

    /** 更新道具按钮 & 次数显示 */
    private void updatePowerUpUI() {
        if (bombCountLabel != null) bombCountLabel.setText("x" + bombCount);
        if (shuffleCountLabel != null) shuffleCountLabel.setText("x" + shuffleCount);
        if (clearCountLabel != null) clearCountLabel.setText("x" + clearCount);

        if (bombBtn != null) bombBtn.setEnabled(bombCount > 0);
        if (shuffleBtn != null) shuffleBtn.setEnabled(shuffleCount > 0);
        if (clearBtn != null) clearBtn.setEnabled(clearCount > 0);
    }

    public void decreaseStep() {
        stepsLeft--;
        stepsLabel.setText("Steps: " + stepsLeft);

        if (stepsLeft <= 0) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(stepsLabel);
            if (frame != null) frame.dispose();

            new EndWindow("<html>Game Over!<br>No steps remaining.</html>").setVisible(true);
        }
    }

    public int getCellW() { return CELL_W; }
    public int getCellH() { return CELL_H; }
    public int getHGap() { return HGAP; }
    public int getVGap() { return VGAP; }
    public int getCols() { return COLS; }
    public int getOriginX() { return ORIGIN_X; }
    public int getOriginY() { return ORIGIN_Y; }
}
