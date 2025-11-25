import javax.swing.JLabel;
import java.util.List;

public final class ScoreManager {

    private static int score = 0;
    private static JLabel boundLabel; // 分数标签

    // ===== 通关目标相关 =====
    public interface GoalListener {
        void onGoalReached(int finalScore);
    }

    private static int goalScore = 0;
    private static GoalListener goalListener;

    private ScoreManager() {}

    // 绑定分数 UI
    public static void bindLabel(JLabel label) {
        boundLabel = label;
        updateLabel();
    }

    private static void updateLabel() {
        if (boundLabel != null) {
            boundLabel.setText("Score: " + score);
        }
    }

    public static void reset() {
        score = 0;
        goalScore = 0;
        goalListener = null;
        updateLabel();
    }

    public static void addScore(int amount) {
        if (amount <= 0) return;
        score += amount;
        updateLabel();
        checkGoal();
    }

    public static int getScore() {
        return score;
    }

    // 设置通关目标
    public static void setGoal(int targetScore, GoalListener listener) {
        goalScore = targetScore;
        goalListener = listener;
        checkGoal();
    }

    private static void checkGoal() {
        if (goalListener != null && goalScore > 0 && score >= goalScore) {
            GoalListener l = goalListener;
            goalListener = null;
            l.onGoalReached(score);
        }
    }

    // 计算连锁得分（保持你原来的逻辑）
    public static int computeComboScore(List<Integer> groupSizes, int chainIndex) {
        int total = 0;
        int comboLevel = Math.max(0, chainIndex - 1);

        for (int size : groupSizes) {
            if (size < 3) continue;

            int base = size;
            int sizeBonus = 0;
            if (size == 4) {
                sizeBonus = 2;
            } else if (size >= 5) {
                sizeBonus = 5 + (size - 5);
            }

            int comboBonus = comboLevel * size;

            total += base + sizeBonus + comboBonus;
        }

        return total;
    }
}
