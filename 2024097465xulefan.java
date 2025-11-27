/*
 * Match3_Items.java
 * 示例：开心消消乐类游戏的道具（道具/技能/消除器）实现
 * 单文件示例，包含：
 * - 网格和格子（Cell, GameBoard）简化实现
 * - 道具接口与具体道具（Bomb, LineClear, ColorClear, Shuffle）
 * - ItemManager 用于触发道具
 * - 一个 main() 演示如何触发道具
 *
 * 说明：这是作业示例，便于理解道具设计与测试。你可以把此文件拆分为多个类文件并纳入你自己的项目。
 */

import java.util.*;

// 一个简单的格子类，包含颜色（用int表示）和是否空
class Cell {
    int color; // 0 表示空，1..N 表示不同颜色
    boolean isEmpty() { return color == 0; }
    Cell(int color) { this.color = color; }
    @Override
    public String toString() { return isEmpty() ? "." : String.valueOf(color); }
}

// 简化的游戏面板，用行*列网格实现
class GameBoard {
    final int rows, cols;
    final Cell[][] board;
    Random rand = new Random();

    GameBoard(int rows, int cols, int colorKinds) {
        this.rows = rows; this.cols = cols;
        board = new Cell[rows][cols];
        fillRandom(colorKinds);
    }

    // 用随机颜色填充（颜色从1到colorKinds）
    void fillRandom(int colorKinds) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board[r][c] = new Cell(rand.nextInt(colorKinds) + 1);
            }
        }
    }

    boolean inBounds(int r, int c) { return r >= 0 && c >= 0 && r < rows && c < cols; }

    Cell get(int r, int c) { return inBounds(r,c) ? board[r][c] : null; }

    void setEmpty(int r, int c) { if (inBounds(r,c)) board[r][c].color = 0; }

    // 简单下落算法：每列向下坠落并在顶部填充随机颜色
    void collapseAndRefill(int colorKinds) {
        for (int c = 0; c < cols; c++) {
            int write = rows - 1;
            for (int r = rows - 1; r >= 0; r--) {
                if (!board[r][c].isEmpty()) {
                    if (write != r) board[write][c].color = board[r][c].color;
                    write--;
                }
            }
            while (write >= 0) {
                board[write][c].color = rand.nextInt(colorKinds) + 1;
                write--;
            }
        }
    }

    void printBoard() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) System.out.print(board[r][c] + " ");
            System.out.println();
        }
    }
}

// 道具类型枚举
enum ItemType { BOMB, LINE_CLEAR, COLOR_CLEAR, SHUFFLE }

// 道具接口：所有道具都要实现 useOn(board, r, c)
interface Item {
    ItemType type();
    // 在 board 上以 (r,c) 为目标使用该道具。返回被消除或改变的格子数量（或影响项数量）
    int useOn(GameBoard board, int r, int c);
}

// 基础道具抽象类（可选）
abstract class AbstractItem implements Item {
    protected final String name;
    AbstractItem(String name) { this.name = name; }
    public String toString() { return name; }
}

// 典型道具：炸弹（消除以目标为中心的 radius 半径范围内所有格子）
class BombItem extends AbstractItem {
    final int radius;
    BombItem(int radius) { super("Bomb(r="+radius+")"); this.radius = radius; }
    public ItemType type() { return ItemType.BOMB; }
    public int useOn(GameBoard board, int r, int c) {
        int removed = 0;
        for (int dr = -radius; dr <= radius; dr++) {
            for (int dc = -radius; dc <= radius; dc++) {
                int nr = r + dr, nc = c + dc;
                if (board.inBounds(nr,nc) && !board.get(nr,nc).isEmpty()) {
                    board.setEmpty(nr,nc); removed++; }
            }
        }
        return removed;
    }
}

// 横或竖向消行道具
class LineClearItem extends AbstractItem {
    final boolean horizontal; // true 表示横向整行，false 表示纵向整列
    LineClearItem(boolean horizontal) { super(horizontal ? "LineClear(H)" : "LineClear(V)"); this.horizontal = horizontal; }
    public ItemType type() { return ItemType.LINE_CLEAR; }
    public int useOn(GameBoard board, int r, int c) {
        int removed = 0;
        if (horizontal) {
            for (int cc = 0; cc < board.cols; cc++) if (!board.get(r,cc).isEmpty()) { board.setEmpty(r,cc); removed++; }
        } else {
            for (int rr = 0; rr < board.rows; rr++) if (!board.get(rr,c).isEmpty()) { board.setEmpty(rr,c); removed++; }
        }
        return removed;
    }
}

// 消除单一颜色道具（比如彩球）
class ColorClearItem extends AbstractItem {
    final int targetColor; // 如果 targetColor==0，表示以触点颜色为目标
    ColorClearItem(int targetColor) { super("ColorClear("+targetColor+")"); this.targetColor = targetColor; }
    public ItemType type() { return ItemType.COLOR_CLEAR; }
    public int useOn(GameBoard board, int r, int c) {
        int colorToRemove = targetColor;
        if (colorToRemove == 0) {
            Cell cell = board.get(r,c);
            if (cell == null || cell.isEmpty()) return 0;
            colorToRemove = cell.color;
        }
        int removed = 0;
        for (int rr = 0; rr < board.rows; rr++) {
            for (int cc = 0; cc < board.cols; cc++) {
                if (!board.get(rr,cc).isEmpty() && board.get(rr,cc).color == colorToRemove) {
                    board.setEmpty(rr,cc); removed++; }
            }
        }
        return removed;
    }
}

// 重新洗牌道具（将未消除的颜色随机打乱位置）
class ShuffleItem extends AbstractItem {
    ShuffleItem() { super("Shuffle"); }
    public ItemType type() { return ItemType.SHUFFLE; }
    public int useOn(GameBoard board, int r, int c) {
        // 采集所有非空颜色并打散后重新放回
        List<Integer> colors = new ArrayList<>();
        for (int rr = 0; rr < board.rows; rr++) for (int cc = 0; cc < board.cols; cc++) if (!board.get(rr,cc).isEmpty()) colors.add(board.get(rr,cc).color);
        Collections.shuffle(colors, board.rand);
        int idx = 0;
        for (int rr = 0; rr < board.rows; rr++) for (int cc = 0; cc < board.cols; cc++) {
            if (!board.get(rr,cc).isEmpty()) board.get(rr,cc).color = colors.get(idx++);
        }
        return colors.size();
    }
}

// === 新增：商店系统 ===
class Shop {
    Map<ItemType, Integer> price = new EnumMap<>(ItemType.class);

    Shop() {
        price.put(ItemType.BOMB, 50);
        price.put(ItemType.LINE_CLEAR, 60);
        price.put(ItemType.COLOR_CLEAR, 80);
    }

    boolean buy(ItemManager manager, Player player, ItemType type) {
        int cost = price.getOrDefault(type, 9999);
        if (player.coins < cost) {
            System.out.println("金币不足，无法购买 " + type);
            return false;
        }
        player.coins -= cost;
        manager.add(type, 1);
        System.out.println("购买成功 → " + type + "，花费金币：" + cost);
        return true;
    }

    void showShop() {
        System.out.println("===== 道具商店 =====");
        for (var e : price.entrySet()) {
            System.out.println(e.getKey() + " : " + e.getValue() + " 金币");
        }
        System.out.println("====================");
    }
}

// 新增玩家类
class Player {
    int coins = 200; // 初始金币
}

// 道具管理器：持有背包或道具槽并执行道具
class ItemManager {
    Map<ItemType, Integer> inventory = new EnumMap<>(ItemType.class);
    ItemManager() { for (ItemType t : ItemType.values()) inventory.put(t, 0); }

    void add(ItemType t, int count) { inventory.put(t, inventory.getOrDefault(t,0)+count); }
    boolean has(ItemType t) { return inventory.getOrDefault(t,0) > 0; }
    boolean consume(ItemType t) {
        int cur = inventory.getOrDefault(t,0);
        if (cur <= 0) return false;
        inventory.put(t, cur-1); return true;
    }

    // 使用道具：如果消耗成功，则调用道具并返回影响数
    int use(Item item, GameBoard board, int r, int c, int colorKinds) {
        if (!has(item.type())) {
            System.out.println("没有道具: " + item);
            return 0;
        }
        boolean ok = consume(item.type());
        if (!ok) return 0;
        int effected = item.useOn(board, r, c);
        // 一次基础后处理：坠落并补充新色
        board.collapseAndRefill(colorKinds);
        return effected;
    }

    @Override
    public String toString() { return "Inventory: " + inventory.toString(); }
}

// 一个演示 main，用于作业提交时展示道具如何工作
public class Match3_Items {
    public static void main(String[] args) {
        int rows = 6, cols = 6, colors = 5;
        GameBoard board = new GameBoard(rows, cols, colors);
        ItemManager manager = new ItemManager();

        // 给玩家几件道具
        manager.add(ItemType.BOMB, 2);
        manager.add(ItemType.LINE_CLEAR, 1);
        manager.add(ItemType.COLOR_CLEAR, 1);
        manager.add(ItemType.SHUFFLE, 1);

        System.out.println(manager);
        System.out.println("初始棋盘：");
        board.printBoard();

        // 使用炸弹在 (2,2)
        Item bomb = new BombItem(1);
        System.out.println("\n使用炸弹在 (2,2) -> 移除: " + manager.use(bomb, board, 2, 2, colors));
        board.printBoard();

        // 使用横向清行在 (3,0)
        Item line = new LineClearItem(true);
        System.out.println("\n使用横向清行在 (3,0) -> 移除: " + manager.use(line, board, 3, 0, colors));
        board.printBoard();

        // 使用颜色清除，以触点颜色
        Item colorClear = new ColorClearItem(0);
        System.out.println("\n使用颜色清除在 (1,1) -> 移除: " + manager.use(colorClear, board, 1, 1, colors));
        board.printBoard();

        // 使用洗牌
        Item shuffle = new ShuffleItem();
        System.out.println("\n使用洗牌 -> 影响格子数: " + manager.use(shuffle, board, 0, 0, colors));
        board.printBoard();

        System.out.println("\n最终背包:");
        System.out.println(manager);
    }
}
