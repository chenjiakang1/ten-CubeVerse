import javax.swing.*;
import java.awt.*;

public class MainWindowBack extends JPanel {

    private Image backgroundImage;

    public MainWindowBack(String imagePath) {
        backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage(); //从resources里读取背景图
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 绘制背景图片，自动缩放到面板大小
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
