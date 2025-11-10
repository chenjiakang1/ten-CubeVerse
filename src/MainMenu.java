import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {

    private JComboBox<String> difficultyBox;

    public MainMenu() {
        super("CubeVerse - Main Menu");
        setSize(400, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // 背景面板（复用你的 MainWindowBack）
        MainWindowBack bg = new MainWindowBack("/MainWindowBack.png");
        bg.setLayout(null);
        bg.setBounds(0, 0, 400, 700);
        setContentPane(bg);

        // 居中的半透明容器
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBounds(50, 140, 300, 420); // 在背景上大致居中
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

        difficultyBox = new JComboBox<>(new String[] {
                "Easy (5×5)", "Normal (6×6)", "Hard (7×7)", "Insane (8×8)"
        });
        difficultyBox.setMaximumSize(new Dimension(240, 36));
        difficultyBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(difficultyBox);
        content.add(Box.createVerticalStrut(24));

        // Start 按钮
        JButton startBtn = new JButton("Start Game");
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.setPreferredSize(new Dimension(200, 40));
        startBtn.setMaximumSize(new Dimension(240, 40));
        startBtn.addActionListener(e -> startGame());
        content.add(startBtn);
        content.add(Box.createVerticalStrut(12));

        // Exit 按钮
        JButton exitBtn = new JButton("Exit");
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setPreferredSize(new Dimension(200, 40));
        exitBtn.setMaximumSize(new Dimension(240, 40));
        exitBtn.addActionListener(e -> System.exit(0));
        content.add(exitBtn);
    }

    private void startGame() {
        int idx = difficultyBox.getSelectedIndex(); // 0..3
        int difficulty;
        switch (idx) {
            case 0: difficulty = 1; break;
            case 1: difficulty = 2; break;
            case 2: difficulty = 3; break;
            case 3: difficulty = 4; break;
            default: difficulty = 1;
        }

        // 打开你的游戏窗口（使用你已有的 MainWindow API）
        MainWindow game = new MainWindow(difficulty);
        game.setVisible(true);

        // 关闭菜单窗口
        dispose();
    }
}
