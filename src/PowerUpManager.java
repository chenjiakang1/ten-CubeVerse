package src;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 道具逻辑：炸弹 / 打乱 / 同色消除
 * 和你现在的 ImageButton / MainWindow 版本是对得上的，不会再有 40 多个错那种情况。
 */
public class PowerUpManager {

    /** 💣 炸弹：以 center 为中心清除 3x3 区域 */
    public static void useBomb(JPanel parent, ImageButton center) {
        if (parent == null || center == null) return;

        // 用行列来算 3x3（比单纯像素更稳定）
        int centerRow = center.currentRow();
        int centerCol = center.currentCol();

        int minRow = centerRow - 1;
        int maxRow = centerRow + 1;
        int minCol = centerCol - 1;
        int maxCol = centerCol + 1;

        List<Component> toRemove = new ArrayList<>();

        for (Component c : parent.getComponents()) {
            if (c instanceof ImageButton && c.isVisible()) {
                ImageButton b = (ImageButton) c;
                int r = b.currentRow();
                int col = b.currentCol();
                if (r >= minRow && r <= maxRow && col >= minCol && col <= maxCol) {
                    toRemove.add(b);
                }
            }
        }

        if (toRemove.isEmpty()) return;

        for (Component c : toRemove) {
            parent.remove(c);
        }
        parent.revalidate();
        parent.repaint();
    }

    /** 🔀 打乱：随机打乱所有可见方块的位置 */
    public static void useShuffle(JPanel parent) {
        if (parent == null) return;

        List<ImageButton> list = new ArrayList<>();
        for (Component c : parent.getComponents()) {
            if (c instanceof ImageButton && c.isVisible()) {
                list.add((ImageButton) c);
            }
        }
        if (list.size() <= 1) return;

        // 记录当前每个方块所在的行列
        List<Point> rcList = new ArrayList<>();
        for (ImageButton b : list) {
            rcList.add(new Point(b.currentRow(), b.currentCol()));
        }

        // 随机打乱这些目标位置
        Collections.shuffle(rcList);

        // 让每个方块移动到新的格子（不做动画，直接 setLocation）
        for (int i = 0; i < list.size(); i++) {
            ImageButton b = list.get(i);
            Point rc = rcList.get(i);
            Point loc = b.locationOfCell(rc.x, rc.y);
            b.setLocation(loc);
        }

        parent.revalidate();
        parent.repaint();
    }

    /** 🎨 清除与 source 同类型的全部方块 */
    public static void useClearSameType(JPanel parent, ImageButton source) {
        if (parent == null || source == null) return;

        String type = source.getType();   // 你刚发的 ImageButton 里有 getType()
        if (type == null) return;

        List<Component> toRemove = new ArrayList<>();

        for (Component c : parent.getComponents()) {
            if (c instanceof ImageButton && c.isVisible()) {
                ImageButton b = (ImageButton) c;
                if (type.equals(b.getType())) {
                    toRemove.add(b);
                }
            }
        }

        if (toRemove.isEmpty()) return;

        for (Component c : toRemove) {
            parent.remove(c);
        }
        parent.revalidate();
        parent.repaint();
    }
}
