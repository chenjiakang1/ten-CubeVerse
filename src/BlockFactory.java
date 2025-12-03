import javax.swing.*;
import java.util.Random;

public class BlockFactory {

    private static final Random RND = new Random();

    /**
     * 生成新的方块（使用 allowedImages）
     * allowedImages = MainWindow.currentImagePaths
     */
    public static ImageButton createRandomBlock(
            String[] allowedImages, //  当前难度允许的图片
            int cellW, int cellH,
            int originX, int originY,
            int cols,
            int hgap, int vgap,
            SwapManager manager
    ) {
        //  用 allowedImages，而不是 10 种固定图片
        String path = allowedImages[RND.nextInt(allowedImages.length)];

        ImageButton btn = new ImageButton(path, cellW, cellH);

        btn.configureGrid(cellW, cellH, originX, originY, cols, hgap, vgap);
        btn.snapToGrid();

        // 让新方块可以参与交换
        if (manager != null) {
            btn.addActionListener(manager);
        }

        return btn;
    }
}
