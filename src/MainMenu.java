import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MainMenu extends JFrame {

    private JComboBox<String> difficultyBox;

    public MainMenu() {
        super("CubeVerse - Main Menu");
        setSize(400, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // 背景
        MainWindowBack bg = new MainWindowBack("/MainWindowBack.png");
        bg.setLayout(null);
        bg.setBounds(0, 0, 400, 700);
        setContentPane(bg);

        // 内容容器
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBounds(50, 140, 300, 420);
        bg.add(content);

        // 标题
        JLabel title = new JLabel("CubeVerse", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        content.add(title);
        content.add(Box.createVerticalStrut(30));

        // 难度选择
        JLabel diffLabel = new JLabel("Difficulty:", SwingConstants.LEFT);
        diffLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        diffLabel.setForeground(Color.WHITE);
        diffLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        content.add(diffLabel);
        content.add(Box.createVerticalStrut(8));

        difficultyBox = new JComboBox<>(new String[]{
                "Easy (5×5)", "Normal (6×6)", "Hard (7×7)", "Insane (8×8)"
        });
        difficultyBox.setMaximumSize(new Dimension(240, 36));
        difficultyBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(difficultyBox);
        content.add(Box.createVerticalStrut(24));

        // ✅ 使用图片按钮
        JButton startBtn = createImageButton("/button.png", "/button.png", "Start Game");
        startBtn.addActionListener(e -> startGame());
        content.add(startBtn);
        content.add(Box.createVerticalStrut(12));

        JButton exitBtn = createImageButton("/button.png", "/button.png", "Exit");
        exitBtn.addActionListener(e -> System.exit(0));
        content.add(exitBtn);
    }

    /** 创建带背景图片和文字的按钮 */
    private JButton createImageButton(String normalPath, String pressedPath, String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalTextPosition(SwingConstants.CENTER); // 文字居中
        btn.setVerticalTextPosition(SwingConstants.CENTER);
        btn.setForeground(Color.WHITE); // 文字颜色
        btn.setFont(new Font("Arial", Font.BOLD, 18));

        // 加载图片
        URL normalURL = getClass().getResource(normalPath);
        URL pressedURL = getClass().getResource(pressedPath);

        if (normalURL != null) {
            btn.setIcon(new ImageIcon(new ImageIcon(normalURL).getImage()
                    .getScaledInstance(240, 60, Image.SCALE_SMOOTH)));
        }
        if (pressedURL != null) {
            btn.setPressedIcon(new ImageIcon(new ImageIcon(pressedURL).getImage()
                    .getScaledInstance(240, 60, Image.SCALE_SMOOTH)));
        }

        // 去掉按钮默认边框与背景
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);

        // 固定尺寸
        btn.setPreferredSize(new Dimension(240, 60));
        btn.setMaximumSize(new Dimension(240, 60));

        return btn;
    }

    private void startGame() {
        int idx = difficultyBox.getSelectedIndex();
        int difficulty;

        switch (idx) {
            case 1:
                difficulty = 2;
                break;
            case 2:
                difficulty = 3;
                break;
            case 3:
                difficulty = 4;
                break;
            default:
                difficulty = 1;
        }

        MainWindow game = new MainWindow(difficulty);
        game.setVisible(true);
        dispose();
    }

}
