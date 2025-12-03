import javax.swing.*;
import java.awt.*;

public class GameRules2Window extends JFrame {

    public GameRules2Window() {
        super("Item Rules");
        setSize(400, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // === 背景 ===
        MainWindowBack bg = new MainWindowBack("/MainWindowBack.png");
        bg.setLayout(null);
        setContentPane(bg);

        // ========== 标题 ==========
        JLabel title = new JLabel("Item Rules", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 20, 400, 40);
        bg.add(title);

        // ========== 文本内容（道具说明） ==========
        JTextArea text = new JTextArea();
        text.setEditable(false);
        text.setForeground(Color.WHITE);
        text.setBackground(new Color(0, 0, 0, 120));
        text.setFont(new Font("Arial", Font.PLAIN, 14));

        text.setText(
                "[CubeVerse Item Effects]\n\n" +

                        "Bomb\n" +
                        "   - Clears a cross-area around the selected block.\n" +
                        "   - Gives +10 score.\n\n" +

                        "Color Clear\n" +
                        "   - Removes all blocks of the selected color.\n" +
                        "   - Gives +10 score.\n\n" +

                        "Extra Step\n" +
                        "   - Adds +5 steps when used.\n\n" +

                        "[Item Purchase Rules]\n\n" +
                        "Each item costs 2 coins.\n" +
                        "Coins are earned by clearing stages:\n\n" +

                        "Tip: Smart item usage can help build strong combos.\n"
        );

        JScrollPane scroll = new JScrollPane(text);
        scroll.setBounds(20, 80, 360, 360);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        bg.add(scroll);

        // ========== Back to Menu 按钮 ==========
        JButton backBtn = createImageButton("/button.png", "/button.png", "Back to Menu");
        backBtn.setBounds(120, 470, 160, 40);
        backBtn.addActionListener(e -> {
            dispose();
            new MainMenu().setVisible(true);
        });
        bg.add(backBtn);

    }


    /** 复用主菜单按钮风格 */
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
