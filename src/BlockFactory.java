import javax.swing.*;
import java.util.Random;

public class BlockFactory {

    private static final String[] IMAGE_PATHS = {
            "/Block_001.png", "/Block_002.png", "/Block_003.png", "/Block_004.png", "/Block_005.png",
            "/Block_006.png", "/Block_007.png", "/Block_008.png", "/Block_009.png", "/Block_010.png"
    };

    private static final Random RND = new Random();

    // 推荐使用：生成带网格 + 监听器的方块
    public static ImageButton createRandomBlock(
            int cellW, int cellH,
            int originX, int originY,
            int cols,
            int hgap, int vgap,
            SwapManager manager   // ⭐⭐ 必须加入
    ) {
        String path = IMAGE_PATHS[RND.nextInt(IMAGE_PATHS.length)];
        ImageButton btn = new ImageButton(path, cellW, cellH);
        btn.configureGrid(cellW, cellH, originX, originY, cols, hgap, vgap);
        btn.snapToGrid();

        // 🔥 加上核心功能（补块后就能交换）
        if (manager != null) {
            btn.addActionListener(manager);
        }

        return btn;
    }
}
