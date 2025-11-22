import javax.swing.JLabel;
import java.util.List;

public final class ScoreManager {

    private static int score = 0;
    private static JLabel boundLabel; // 绑定 UI 上的分数标签

    // ===== 通关目标相关 =====
    public interface GoalListener {
        void onGoalReached(int finalScore);
    }

    private static int goalScore = 0;            // 目标分数（0 表示不开启）
    private static GoalListener goalListener;    // 通关回调

    private ScoreManager() {}

    public static void bindLabel(JLabel label) {
        boundLabel = label;
        updateLabel();   // 绑定时先刷新一次显示
    }

    private static void updateLabel() {
        if (boundLabel != null) {
            boundLabel.setText("Score: " + score);
        }
    }

    // ⭐ 设置目标分数 + 通关回调
    public static void setGoal(int targetScore, GoalListener listener) {
        goalScore = targetScore;
        goalListener = listener;
        checkGoal(); // 以防已经提前达到
    }

    // ⭐ 检查是否到达通关分数
    private static void checkGoal() {
        if (goalListener != null && goalScore > 0 && score >= goalScore) {
            GoalListener l = goalListener;
            goalListener = null; // 只触发一次
            l.onGoalReached(score);
        }
    }



    public static void reset() {
        score = 0;
        goalScore = 0;
        goalListener = null;
        updateLabel();   // 每次重置顺便更新 UI
    }

    public static void addScore(int amount) {
        if (amount <= 0) return;
        score += amount;
        updateLabel();
        checkGoal();        // ⭐ 每次加分后检查是否通关
    }

    public static int getScore() {
        return score;
    }
    // ⭐ 新增：根据每个连通块大小 + 当前连锁层数，计算本轮消除得分
    public static int computeComboScore(List<Integer> groupSizes, int chainIndex) {
        int total = 0;
        int comboLevel = Math.max(0, chainIndex - 1); // 第1次=0，加成从第2次开始

        for (int size : groupSizes) {
            if (size < 3) continue;

            // 1) 基础分
            int base = size;

            // 2) 大块加成
            int sizeBonus = 0;
            if (size == 4) {
                sizeBonus = 2;
            } else if (size >= 5) {
                sizeBonus = 5 + (size - 5); // 5连+5，再多每块+1
            }

            // 3) 连锁加成
            int comboBonus = comboLevel * size;

            total += base + sizeBonus + comboBonus;
        }

        return total;
    }
}
