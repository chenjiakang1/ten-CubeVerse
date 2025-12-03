import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/** 4 邻接三消：同类型连通块大小 >= 3 即消除；消除后重力下落 -> 补新块 -> 连锁 */
public class Match3Manager {
    private static int comboStep = 0;   // 当前连锁层数：1,2,3...

    /** 入口：扫描 parent 中全部可见 ImageButton，执行三消->下落->连锁 */
    /** 兼容旧调用版本（自动读取 grid 参数） */
    public static void removeMatches(JPanel parent) {
        // 找到一个 ImageButton 并读取它的 grid 参数
        for (Component c : parent.getComponents()) {
            if (c instanceof ImageButton) {
                ImageButton b = (ImageButton) c;

                // 读取配置
                int cellW   = b.getCellW();
                int cellH   = b.getCellH();
                int originX = b.getOriginX();
                int originY = b.getOriginY();
                int cols    = b.getCols();
                int hgap    = b.getHgap();
                int vgap    = b.getVgap();

                // 调用真正的主版本
                removeMatches(parent, cellW, cellH, originX, originY, cols, hgap, vgap);
                return;
            }
        }

        // 如果没有按钮（空面板），那就调用默认值
        removeMatches(parent, 30, 30, 0, 0, 5, 5, 5);
    }

    public static void removeMatches(
            JPanel parent,
            int cellW, int cellH,
            int originX, int originY,
            int cols,
            int hgap, int vgap
    ) {
        Map<Point, ImageButton> grid = buildGrid(parent);
        if (grid.isEmpty()) {
            comboStep = 0; // 棋盘空了，连锁结束
            return;
        }
        Set<Point> visited = new HashSet<>();
        Set<Point> toRemove = new HashSet<>();
        java.util.List<Integer> groupSizes = new java.util.ArrayList<>(); //  记录每个连通块大小
        int[][] DIRS = {{1,0}, {-1,0}, {0,1}, {0,-1}};

        // BFS 搜索连通块
        for (Map.Entry<Point, ImageButton> e : grid.entrySet()) {
            Point start = e.getKey();
            if (visited.contains(start)) continue;

            String type = e.getValue().getType();
            Queue<Point> q = new ArrayDeque<>();
            java.util.List<Point> comp = new java.util.ArrayList<>();
            visited.add(start);
            q.add(start);

            while (!q.isEmpty()) {
                Point p = q.poll();
                comp.add(p);

                for (int[] d : DIRS) {
                    Point np = new Point(p.x + d[0], p.y + d[1]);
                    ImageButton nb = grid.get(np);
                    if (nb != null && !visited.contains(np) && java.util.Objects.equals(nb.getType(), type)) {
                        visited.add(np);
                        q.add(np);
                    }
                }
            }

            if (comp.size() >= 3) {
                toRemove.addAll(comp);
                groupSizes.add(comp.size()); //  每个连通块的大小
            }
        }

        // ▶ 没有可消，连锁结束
        if (toRemove.isEmpty()) {
            comboStep = 0;
            return;
        }
        // ▶ 有消除，本轮属于连锁第 N 次
        comboStep++;
        //  计算本次消除的全部奖励分数（基础分 + size bonus + combo bonus）
        int gained = ScoreManager.computeComboScore(groupSizes, comboStep);
        ScoreManager.addScore(gained);
        SoundManager.playDestroy();

        // 删除方块
        for (Point p : toRemove) {
            ImageButton b = grid.get(p);
            if (b != null) parent.remove(b);
            grid.remove(p);
        }
        parent.revalidate();
        parent.repaint();

        // 下落 + 补新块 + 连锁
        applyGravityThenChain(parent, grid, 500, cellW, cellH, originX, originY, cols, hgap, vgap);
    }

    /** (row,col)->ImageButton */
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

    /** 下落->补新块->连锁 */
    private static void applyGravityThenChain(
            JPanel parent, Map<Point, ImageButton> grid, int durationMs,
            int cellW, int cellH,
            int originX, int originY,
            int cols,
            int hgap, int vgap
    ) {
        if (grid.isEmpty()) return;

        int minCol = Integer.MAX_VALUE, maxCol = Integer.MIN_VALUE, maxRow = Integer.MIN_VALUE;
        for (Point p : grid.keySet()) {
            minCol = Math.min(minCol, p.y);
            maxCol = Math.max(maxCol, p.y);
            maxRow = Math.max(maxRow, p.x);
        }

        AtomicInteger running = new AtomicInteger(0);

        /* =====================  第一阶段：重力下落  ===================== */
        for (int col = minCol; col <= maxCol; col++) {
            int writeRow = maxRow;
            for (int row = maxRow; row >= 0; row--) {
                Point p = new Point(row, col);
                ImageButton b = grid.get(p);
                if (b == null) continue;

                if (row != writeRow) {
                    int targetRow = writeRow;
                    running.incrementAndGet();
                    SoundManager.playMove();

                    b.fallToRow(targetRow, durationMs, () -> {
                        if (running.decrementAndGet() == 0) {
                            removeMatches(parent, cellW, cellH, originX, originY, cols, hgap, vgap);
                        }
                    });

                    grid.remove(p);
                    grid.put(new Point(targetRow, col), b);
                }
                writeRow--;
            }
        }

        /* =====================  第二阶段：补充新方块  ===================== */
        for (int col = minCol; col <= maxCol; col++) {
            int count = 0;
            for (Point p : grid.keySet()) if (p.y == col) count++;

            int need = (maxRow + 1) - count;
            if (need <= 0) continue;

            for (int i = 0; i < need; i++) {
                int spawnRow  = -1 - i;
                int targetRow = need - 1 - i;

                ImageButton newB = BlockFactory.createRandomBlock(
                        cellW, cellH, originX, originY, cols, hgap, vgap,
                        findSwapManager(parent)
                );

                newB.moveToGridCell(spawnRow, col);
                parent.add(newB);

                grid.put(new Point(targetRow, col), newB);

                running.incrementAndGet();

                newB.fallToRow(targetRow, durationMs, () -> {
                    if (running.decrementAndGet() == 0) {
                        removeMatches(parent, cellW, cellH, originX, originY, cols, hgap, vgap);
                    }
                });
            }
        }

        parent.revalidate();
        parent.repaint();
    }

    private static SwapManager findSwapManager(JPanel parent) {
        for (Component c : parent.getComponents()) {
            if (c instanceof ImageButton) {
                for (ActionListener al : ((ImageButton)c).getActionListeners()) {
                    if (al instanceof SwapManager) {
                        return (SwapManager) al;
                    }
                }
            }
        }
        return null; // 正常不会发生
    }

    public static void useBomb(ImageButton center) {
        JPanel parent = (JPanel) center.getParent();
        if (parent == null) return;

        Map<Point, ImageButton> grid = buildGrid(parent);
        if (grid.isEmpty()) return;

        int cr = center.currentRow();
        int cc = center.currentCol();

        // 删除符合范围的块（曼哈顿距离 ≤ 2）
        for (Map.Entry<Point, ImageButton> e : grid.entrySet()) {
            Point p = e.getKey();
            int dist = Math.abs(p.x - cr) + Math.abs(p.y - cc);
            if (dist <= 2) {
                parent.remove(e.getValue());
            }
        }

        parent.revalidate();
        parent.repaint();

        // 道具固定加十积分
        ScoreManager.addScore(10);

        // ★★★ 重新读取新的 grid ★★★
        grid = buildGrid(parent);

        // ★★★ 强制触发重力 ★★★
        applyGravityThenChain(
                parent,
                grid,
                500,
                center.getCellW(),
                center.getCellH(),
                center.getOriginX(),
                center.getOriginY(),
                center.getCols(),
                center.getHgap(),
                center.getVgap()
        );
    }

    public static void useColorClear(ImageButton target) {
        JPanel parent = (JPanel) target.getParent();
        if (parent == null) return;

        String type = target.getType();
        if (type == null) return;

        Map<Point, ImageButton> grid = buildGrid(parent);

        // 删除所有同色块
        for (Map.Entry<Point, ImageButton> e : grid.entrySet()) {
            if (type.equals(e.getValue().getType())) {
                parent.remove(e.getValue());
            }
        }

        parent.revalidate();
        parent.repaint();

        // 道具固定加十积分
        ScoreManager.addScore(10);

        // ★★★ 重新读取新的 grid ★★★
        grid = buildGrid(parent);

        // ★★★ 强制触发重力、补新块、连锁 ★★★
        applyGravityThenChain(
                parent,
                grid,
                500,
                target.getCellW(),
                target.getCellH(),
                target.getOriginX(),
                target.getOriginY(),
                target.getCols(),
                target.getHgap(),
                target.getVgap()
        );
    }
}
