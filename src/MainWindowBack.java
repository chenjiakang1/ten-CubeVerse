import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class MainWindowBack extends JPanel {

    private Image backgroundImage;

    public MainWindowBack(String imagePath) {
        // 调试：看看 JVM 实际能不能找到这个资源
        URL url = getClass().getResource(imagePath);
        System.out.println("DEBUG MainWindowBack resource = " + imagePath + " -> " + url);

        if (url == null) {
            System.err.println("❌ 背景图没找到: " + imagePath);

            // 防止空指针，把背景设成一张 1x1 透明图，不再崩溃
            backgroundImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        } else {
            backgroundImage = new ImageIcon(url).getImage();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
