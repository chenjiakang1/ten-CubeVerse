import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import java.awt.event.KeyEvent;
import javax.swing.*;

public class MainWindow {
    // ========== 记分 & 通关字段 ==========
    private JLabel scoreLabel;          // 显示右上角分数
    private int goalScore;          //  该局目标分数
    private boolean levelCleared;   //  防止重复触发通关
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
    private JLabel coinLabel;

    private String mode;        //难度

    private ItemType currentItem = ItemType.NONE;
    private JLabel bombCountLabel;
    private JLabel colorCountLabel;
    private JLabel stepCountLabel;



    public MainWindow(int difficulty) {
        // 新建 MainWindow = 开始一局 ⇒ 分数清零
        ScoreManager.reset();
        // 新开始一局道具恢复初始
        ItemManager.reset();

        switch (difficulty) {
            case 1:
                COLS = 5; COLS_PER_ROW = 5; TOTAL_COUNT = 25;
                goalScore = 100;   // 简单模式通关分
                stepsLeft = 3;    // 简单模式步数
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

            // 2. 添加提示背景图（木牌）→ 在文字之前 add
            ImageIcon hintIcon = new ImageIcon(getClass().getResource("/HintPanel.png"));
            Image hintImg = hintIcon.getImage().getScaledInstance(180, 170, Image.SCALE_SMOOTH);
            JLabel hintBackground = new JLabel(new ImageIcon(hintImg));
            hintBackground.setBounds(WINDOW_WIDTH - 160, 0, 180, 170);
            bgPanel.add(hintBackground);   // 这一步必须在所有文字前



            // 3. 添加文字（自动盖在木牌前面）
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

            coinLabel = new JLabel("Coins: " + CoinManager.getInstance().getCoins());
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

            // 将木牌放到底层（仅高于背景图）
            int deepest = bgPanel.getComponentCount() - 1;
            bgPanel.setComponentZOrder(hintBackground, deepest);

            ScoreManager.setGoal(goalScore, finalScore -> {
                if (levelCleared) return;
                levelCleared = true;

                int coinsEarned = Math.max(1, finalScore / 50);
                CoinManager.getInstance().addCoins(coinsEarned);

                // 关闭当前游戏窗口
                JFrame window = (JFrame) SwingUtilities.getWindowAncestor(scoreLabel);
                if (window != null) window.dispose();

                // 准备结束文字
                String msg = "<html>Stage Cleared!<br>"
                        + "Score: " + finalScore + " / " + goalScore + "<br>"
                        + "Coins earned: " + coinsEarned + "</html>";

                // 打开带背景图的结束界面
                new EndWindow(msg).setVisible(true);
            });

            createButtons(bgPanel, new SwapManager(this, 300), TOTAL_COUNT);

            // ========= 道具栏尺寸 ==========
            int itemSize = 40;
            int gap = 5;
            int count = 3;
            int totalWidth = itemSize * count + gap * (count - 1);

            // ========= 木板背景 =========
            int boardWidth = totalWidth + 80;
            int boardHeight = 130;
            int y = WINDOW_HEIGHT - 120;

            ImageIcon boardIcon = new ImageIcon(getClass().getResource("/Itemback.png"));
            Image boardImg = boardIcon.getImage().getScaledInstance(boardWidth, boardHeight, Image.SCALE_SMOOTH);
            JLabel board = new JLabel(new ImageIcon(boardImg));
            board.setBounds((WINDOW_WIDTH - boardWidth) / 2, y - 25, boardWidth, boardHeight);
            bgPanel.add(board);

            // ========= 道具起始位置（居中） =========
            int startX = (WINDOW_WIDTH - totalWidth) / 2;

            // ========= 数量文本通用参数 =========
            int labelW = 20;
            int labelH = 20;
            int labelY = y + itemSize + 2 - 5;

            // ========= 购买按钮公共设置 =========
            Font buyFont = new Font("Arial", Font.BOLD, 11);

            // ========= Bomb =========
            int bombX = startX;

            JButton bombBtn = new JButton();
            bombBtn.setBounds(bombX, y, itemSize, itemSize);
            setItemButtonIcon(bombBtn, "/Item1.png", itemSize);
            bombBtn.addActionListener(e -> {
                if (ItemManager.useBomb()) {
                    selectItem(ItemType.BOMB);
                    bombCountLabel.setText("x" + ItemManager.getBombCount());
                } else {
                    JOptionPane.showMessageDialog(null, "No Bombs Left!");
                }
            });
            bgPanel.add(bombBtn);

            // Bomb 数量
            bombCountLabel = new JLabel("x" + ItemManager.getBombCount());
            bombCountLabel.setForeground(Color.WHITE);
            bombCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
            bombCountLabel.setBounds(bombX + itemSize/2 - labelW/2, labelY, labelW, labelH);
            bgPanel.add(bombCountLabel);

            // 购买 Bomb
            // 创建 BomB 的购买按钮（使用图片）
            JButton buyBomb = new JButton();
            buyBomb.setBounds(bombX + itemSize/2 - 10, labelY + 20, 22, 22);

            // 设置图片图标
            setItemButtonIcon(buyBomb, "/Item+1button.png", 22);

            // 取消按钮默认效果（去掉背景、边框）
            buyBomb.setBorderPainted(false);
            buyBomb.setContentAreaFilled(false);
            buyBomb.setFocusPainted(false);

            // 点击事件：花2金币 +1 道具
            buyBomb.addActionListener(e -> {
                if (CoinManager.getInstance().removeCoins(2)) {
                    ItemManager.addBomb(1);
                    bombCountLabel.setText("x" + ItemManager.getBombCount());
                    coinLabel.setText("Coins: " + CoinManager.getInstance().getCoins());
                } else {
                    JOptionPane.showMessageDialog(null, "Not enough coins!");
                }
            });

            bgPanel.add(buyBomb);




            // ========= Color Clear =========
            int colorX = startX + itemSize + gap;

            JButton colorBtn = new JButton();
            colorBtn.setBounds(colorX, y, itemSize, itemSize);
            setItemButtonIcon(colorBtn, "/Item2.png", itemSize);
            colorBtn.addActionListener(e -> {
                if (ItemManager.useColorClear()) {
                    selectItem(ItemType.COLOR_CLEAR);
                    colorCountLabel.setText("x" + ItemManager.getColorClearCount());
                } else {
                    JOptionPane.showMessageDialog(null, "No Color-Clear Items Left!");
                }
            });
            bgPanel.add(colorBtn);

            // Color Clear 数量
            colorCountLabel = new JLabel("x" + ItemManager.getColorClearCount());
            colorCountLabel.setForeground(Color.WHITE);
            colorCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
            colorCountLabel.setBounds(colorX + itemSize/2 - labelW/2, labelY, labelW, labelH);
            bgPanel.add(colorCountLabel);

            // 购买 Color Clear
            // ========= ColorClear +1 按钮 =========
            JButton buyColor = new JButton();
            buyColor.setBounds(
                    colorX + itemSize / 2 - 11,
                    labelY + 20,
                    22,
                    22
            );

            // 设为图片按钮
            setItemButtonIcon(buyColor, "/Item+1button.png", 22);

            // 去除按钮背景
            buyColor.setBorderPainted(false);
            buyColor.setContentAreaFilled(false);
            buyColor.setFocusPainted(false);

            // 点击事件：花2金币 +1同色消除
            buyColor.addActionListener(e -> {
                if (CoinManager.getInstance().removeCoins(2)) {
                    ItemManager.addColorClear(1);
                    colorCountLabel.setText("x" + ItemManager.getColorClearCount());
                    coinLabel.setText("Coins: " + CoinManager.getInstance().getCoins());
                } else {
                    JOptionPane.showMessageDialog(null, "Not enough coins!");
                }
            });

            bgPanel.add(buyColor);




            // ========= Extra Steps =========
            int stepX = startX + (itemSize + gap) * 2;

            JButton stepBtn = new JButton();
            stepBtn.setBounds(stepX, y, itemSize, itemSize);
            setItemButtonIcon(stepBtn, "/Item3.png", itemSize);
            stepBtn.addActionListener(e -> {
                if (ItemManager.useExtraStep()) {
                    stepsLeft += 5;
                    stepsLabel.setText("Steps: " + stepsLeft);
                    stepCountLabel.setText("x" + ItemManager.getExtraStepCount());
                    clearItem();
                } else {
                    JOptionPane.showMessageDialog(null, "No Step Items Left!");
                }
            });
            bgPanel.add(stepBtn);

            // 数量
            stepCountLabel = new JLabel("x" + ItemManager.getExtraStepCount());
            stepCountLabel.setForeground(Color.WHITE);
            stepCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
            stepCountLabel.setBounds(stepX + itemSize/2 - labelW/2, labelY, labelW, labelH);
            bgPanel.add(stepCountLabel);

            // 购买 Extra Step
            // ========= Extra Step +1 按钮 =========
            JButton buyStep = new JButton();
            buyStep.setBounds(
                    stepX + itemSize / 2 - 11,
                    labelY + 20,
                    22,
                    22
            );

            // 设为图片按钮
            setItemButtonIcon(buyStep, "/Item+1button.png", 22);

            // 去除按钮背景
            buyStep.setBorderPainted(false);
            buyStep.setContentAreaFilled(false);
            buyStep.setFocusPainted(false);

            // 点击事件：花2金币 +1步数道具
            buyStep.addActionListener(e -> {
                if (CoinManager.getInstance().removeCoins(2)) {
                    ItemManager.addExtraStep(1);
                    stepCountLabel.setText("x" + ItemManager.getExtraStepCount());
                    coinLabel.setText("Coins: " + CoinManager.getInstance().getCoins());
                } else {
                    JOptionPane.showMessageDialog(null, "Not enough coins!");
                }
            });

            bgPanel.add(buyStep);



            // ========= Z-order =========
            bgPanel.setComponentZOrder(board, bgPanel.getComponentCount() - 1);


            addBackToMenu(bgPanel, frame);

            frame.setVisible(true);
        });
    }

    // ========= 封装：设置图片按钮 =========
    private void setItemButtonIcon(JButton btn, String path, int size) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        btn.setIcon(new ImageIcon(img));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
    }

    // 设置当前道具
    public void selectItem(ItemType type) {
        currentItem = type;
        System.out.println("Selected item: " + type);
    }

    // 读取当前道具（给 SwapManager 使用）
    public ItemType getCurrentItem() {
        return currentItem;
    }

    // 使用完道具后重置
    public void clearItem() {
        currentItem = ItemType.NONE;
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

    public void decreaseStep() {
        stepsLeft--;
        stepsLabel.setText("Steps: " + stepsLeft);

        if (stepsLeft <= 0) {

            // 关闭当前游戏窗口（MainWindow）
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(stepsLabel);
            if (frame != null) frame.dispose();

            // 显示带背景图的结算界面
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
