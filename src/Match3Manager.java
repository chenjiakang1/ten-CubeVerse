import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.List;

/**
 * 基于 4 邻接（上下左右）的三消判定：
 * 任意同类型的连通块大小 >= 3 即消除（支持直线、L 形、T 形、十字等）。
 */
public class Match3Manager {

    /** 扫描 parent 中全部可见的 ImageButton，找出同类型 4 邻接连通分量大小>=3 的块并移除。 */
    public static void removeMatches(JPanel parent) {
        // 1) 收集所有按钮并按 (row, col) 建表
        Map<Point, ImageButton> grid = new HashMap<>();
        for (Component c : parent.getComponents()) {
            if (c instanceof ImageButton && c.isVisible()) {
                ImageButton b = (ImageButton) c;
                grid.put(new Point(b.currentRow(), b.currentCol()), b);
            }
        }
        if (grid.isEmpty()) return;

        // 2) 基于 4 邻接的连通分量搜索
        Set<Point> visited = new HashSet<>();
        Set<Point> toRemove = new HashSet<>();
        int[][] DIRS = { {1,0}, {-1,0}, {0,1}, {0,-1} };

        for (Map.Entry<Point, ImageButton> entry : grid.entrySet()) {
            Point start = entry.getKey();
            if (visited.contains(start)) continue;

            String type = entry.getValue().getType();
            Queue<Point> q = new ArrayDeque<>();
            List<Point> comp = new ArrayList<>();
            visited.add(start);
            q.add(start);

            while (!q.isEmpty()) {
                Point p = q.poll();
                comp.add(p);

                for (int[] d : DIRS) {
                    Point np = new Point(p.x + d[0], p.y + d[1]);
                    if (visited.contains(np)) continue;
                    ImageButton nb = grid.get(np);
                    if (nb != null && Objects.equals(nb.getType(), type)) {
                        visited.add(np);
                        q.add(np);
                    }
                }
            }

            if (comp.size() >= 3) {
                toRemove.addAll(comp);
            }
        }

        // 3) 执行移除并刷新（有匹配才播放销毁音效）
        if (!toRemove.isEmpty()) {
            SoundManager.playDestroy(); // ✅ 播放销毁音效
            for (Point p : toRemove) {
                ImageButton b = grid.get(p);
                if (b != null) parent.remove(b);
            }
            parent.revalidate();
            parent.repaint();

            // 如需“连消”，可在这里再次调用：
            // removeMatches(parent);
        }
    }
}
