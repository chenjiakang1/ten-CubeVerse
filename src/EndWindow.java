import javax.swing.*;
import java.awt.*;

public class EndWindow extends JFrame {

    public EndWindow(String message) {

        setTitle("Result");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // ========== 背景图 ==========
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/EndBack.png"));
        Image bgImg = bgIcon.getImage().getScaledInstance(400, 350, Image.SCALE_SMOOTH);
        JLabel bg = new JLabel(new ImageIcon(bgImg));
        bg.setBounds(0, 0, 400, 350);
        bg.setLayout(null);
        add(bg);

        // ========== 文本（显示在木板中间） ==========
        JLabel msg = new JLabel(message, SwingConstants.CENTER);
        msg.setFont(new Font("Arial", Font.BOLD, 18));
        msg.setForeground(Color.WHITE);
        msg.setBounds(50, 200, 300, 60);
        bg.add(msg);

        // ========== 使用 button.png 的按钮 ==========
        ImageIcon btnIcon = new ImageIcon(getClass().getResource("/endbutton.png"));
        Image scaledBtn = btnIcon.getImage().getScaledInstance(200, 100, Image.SCALE_SMOOTH);
        ImageIcon finalBtnIcon = new ImageIcon(scaledBtn);

        JButton back = new JButton("Back to Menu", finalBtnIcon);
        back.setHorizontalTextPosition(SwingConstants.CENTER);
        back.setForeground(Color.WHITE);
        back.setFont(new Font("Arial", Font.BOLD, 14));

        // 去掉按钮边框 & 背景
        back.setBorderPainted(false);
        back.setContentAreaFilled(false);
        back.setFocusPainted(false);

        back.setBounds(135, 270, 130, 45);
        bg.add(back);

        back.addActionListener(e -> {
            dispose();
            new MainMenu().setVisible(true);
        });
    }
}
