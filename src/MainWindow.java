import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MainWindow {
    public static void main(String[] args) {
        JFrame frame = new JFrame("CubeVerse");
        frame.setSize(400, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);

        // ---- 添加背景面板 ----
        MainWindowBack bgPanel = new MainWindowBack("/MainWindowBack.png");
        bgPanel.setLayout(null); // 用绝对布局放按钮
        bgPanel.setBounds(0, 0, 400, 700);
        frame.setContentPane(bgPanel); // 背景作为主面板


        // ---- 创建 SwapManager ----
        SwapManager manager = new SwapManager(300);

        // ---- 批量创建按钮 ----
        List<ImageButton> buttons = new ArrayList<>();
        buttons.add(createImageButton("/Block_001.png", 70, 100, manager));
        buttons.add(createImageButton("/Block_002.png", 105, 100, manager));
        buttons.add(createImageButton("/Block_003.png", 140, 100, manager));
        buttons.add(createImageButton("/Block_004.png", 175, 100, manager));

        // ---- 添加按钮到背景 ----
        for (ImageButton btn : buttons) {
            bgPanel.add(btn);
        }

        frame.setVisible(true);
    }

    private static void removeAllActionListeners(AbstractButton b) {
        for (ActionListener al : b.getActionListeners()) {
            b.removeActionListener(al);
        }
    }

    /**
     * 工具方法：创建带图片、位置、监听的按钮
     * @param path 图片路径（以 / 开头）
     * @param x    初始X坐标
     * @param y    初始Y坐标
     * @param manager SwapManager 统一管理点击与交换
     */
    private static ImageButton createImageButton(String path, int x, int y, SwapManager manager) {
        ImageButton btn = new ImageButton(path, 30, 30);
        btn.setLocation(x, y);

        // 移除旧监听
        for (ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }

        // 绑定统一 SwapManager
        btn.addActionListener(manager);
        return btn;
    }
}
