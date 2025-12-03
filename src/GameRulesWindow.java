import javax.swing.*;
import java.awt.*;

public class GameRulesWindow extends JFrame {

    public GameRulesWindow() {
        super("Game Rules");
        setSize(400, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // === 使用背景 ===
        MainWindowBack bg = new MainWindowBack("/MainWindowBack.png");
        bg.setLayout(null);
        setContentPane(bg);

        // ========== 顶部示例图片 ==========
        ImageIcon icon = new ImageIcon(getClass().getResource("/GameRules.jpg"));
        JLabel exampleImage = new JLabel();
        exampleImage.setIcon(new ImageIcon(icon.getImage().getScaledInstance(300, 160, Image.SCALE_SMOOTH)));
        exampleImage.setBounds(40, 20, 300, 160);
        bg.add(exampleImage);

        // ========== 标题 ==========
        JLabel title = new JLabel("Game Rules", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 180, 400, 40);
        bg.add(title);

        // ========== 文本内容 ==========
        JTextArea text = new JTextArea();
        text.setEditable(false);
        text.setForeground(Color.WHITE);
        text.setBackground(new Color(0, 0, 0, 120));
        text.setFont(new Font("Arial", Font.PLAIN, 12));

        text.setText(
                "[CubeVerse Rules]\n\n" +
                        "1. Choose difficulty (board size & colors).\n\n" +
                        "2. Swap two adjacent blocks.\n\n" +
                        "3. Three or more identical adjacent blocks will auto-clear.\n\n" +
                        "4. Blocks fall down to fill empty spaces.\n\n" +
                        "5. After clearing the stage, every 50 score = 1 coin.\n\n" +
                        "Tip: Chain reactions give more points.\n"
        );

        JScrollPane scroll = new JScrollPane(text);
        scroll.setBounds(20, 230, 360, 260);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        bg.add(scroll);

        // ========== 返回主菜单按钮 ==========
        JButton backBtn = createImageButton("/button.png", "/button.png", "Back to Menu");
        backBtn.setBounds(40, 510, 160, 40);
        backBtn.addActionListener(e -> {
            dispose();
            new MainMenu().setVisible(true);
        });
        bg.add(backBtn);

        // ========== 关闭按钮（使用主菜单按钮图片 + 关闭并打开新窗口） ==========
        JButton closeBtn = createImageButton("/button.png", "/button.png", "Next");
        closeBtn.setBounds(200, 510, 160, 40);

        closeBtn.addActionListener(e -> {
            dispose();                      // 关闭当前窗口

            // 想打开哪一个，就在这里改
            new GameRules2Window().setVisible(true);
        });

        bg.add(closeBtn);
    }


    /** 使用主菜单的图片按钮样式 */
    private JButton createImageButton(String normalPath, String pressedPath, String text) {
        JButton btn = new JButton(text);

        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.CENTER);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 18));

        ImageIcon normalIcon = new ImageIcon(getClass().getResource(normalPath));
        ImageIcon pressedIcon = new ImageIcon(getClass().getResource(pressedPath));

        btn.setIcon(new ImageIcon(normalIcon.getImage().getScaledInstance(160, 40, Image.SCALE_SMOOTH)));
        btn.setPressedIcon(new ImageIcon(pressedIcon.getImage().getScaledInstance(160, 40, Image.SCALE_SMOOTH)));

        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);

        return btn;
    }
}
