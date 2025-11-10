import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Point;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/** 4 邻接三消：同类型连通块大小 >= 3 即消除；消除后重力下落(0.5s)，动画结束自动连消。 */
public class Match3Manager {

    /** 入口：扫描 parent 中全部可见的 ImageButton，执行三消->下落->连锁 */
    public static void removeMatches(JPanel parent) {
        // 1) 收集所有按钮并按 (row, col) 建表
        Map<Point, ImageButton> grid = buildGrid(parent);
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

        // 3) 执行移除 -> 重力 ->（可选）连锁
        if (!toRemove.isEmpty()) {
            SoundManager.playDestroy(); // 销毁音效（可选）
            for (Point p : toRemove) {
                ImageButton b = grid.get(p);
                if (b != null) parent.remove(b);
                grid.remove(p); // 同步从表里移除
            }
            parent.revalidate();
            parent.repaint();

            // 4) 重力：让每列压缩并播放 0.5s 下落动画；动画全部结束后再做一次 removeMatches（连消）
            applyGravityThenChain(parent, grid, 500);
        }
        // 如果 toRemove 为空：这一轮没有可消除的，直接返回（终止连锁）
    }

    /** 按 (row,col) 建表 */
    private static Map<Point, ImageButton> buildGrid(JPanel parent) {
        Map<Point, ImageButton> grid = new HashMap<>();
        for (Component c : parent.getComponents()) {
            if (c instanceof ImageButton && c.isVisible()) {
                ImageButton b = (ImageButton) c;
                grid.put(new Point(b.currentRow(), b.currentCol()), b);
            }
        }
        return grid;
    }

    /** 压缩每一列；触发 0.5s 动画；全部动画完成后自动连消 */
    private static void applyGravityThenChain(JPanel parent, Map<Point, ImageButton> grid, int durationMs) {
        if (grid.isEmpty()) return;

        // 计算列范围、最大行
        int minCol = Integer.MAX_VALUE, maxCol = Integer.MIN_VALUE, maxRow = Integer.MIN_VALUE;
        for (Point p : grid.keySet()) {
            minCol = Math.min(minCol, p.y);
            maxCol = Math.max(maxCol, p.y);
            maxRow = Math.max(maxRow, p.x);
        }

        AtomicInteger running = new AtomicInteger(0);
        boolean anyFell = false;

        // 对每列进行“自底向上”的压缩
        for (int col = minCol; col <= maxCol; col++) {
            int writeRow = maxRow; // 从底部往上“写入”
            for (int row = maxRow; row >= 0; row--) {
                ImageButton b = grid.get(new Point(row, col));
                if (b == null) continue;

                if (row != writeRow) {
                    anyFell = true;
                    SoundManager.playMove(); // 下落音效（可选）
                    running.incrementAndGet();

                    int targetRow = writeRow;
                    // 触发动画：结束时减少计数；所有动画结束后触发下一轮消除（连锁）
                    b.fallToRow(targetRow, durationMs, () -> {
                        if (running.decrementAndGet() == 0) {
                            // 所有下落动画结束，触发下一轮连消
                            removeMatches(parent);
                        }
                    });

                    // 更新哈希表坐标
                    grid.remove(new Point(row, col));
                    grid.put(new Point(targetRow, col), b);
                }
                writeRow--;
            }
        }

        if (anyFell) {
            parent.revalidate();
            parent.repaint();
        } else {
            // 没有下落，说明这一轮结束（不会连锁）
        }
    }
}
