import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwapManager implements ActionListener {
    private final int durationMs;     // 动画时长
    private ImageButton selected = null;
    private boolean swapping = false; // 动画期间节流

    public SwapManager(int durationMs) {
        this.durationMs = Math.max(0, durationMs);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (swapping) return;
        if (!(e.getSource() instanceof ImageButton)) return;

        ImageButton btn = (ImageButton) e.getSource();

        // 第一次点击：进入“准备”
        if (selected == null) {
            selected = btn;
            selected.setReady(true);
            return;
        }

        // 第二次点击：尝试与已选按钮交换
        ImageButton a = selected;
        ImageButton b = btn;

        // 再点自己：取消准备
        if (a == b) {
            a.setReady(false);
            selected = null;
            return;
        }

        // 若任一处于动画中，忽略
        if (a.isAnimating() || b.isAnimating()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        // ===== 新增：显式检查是否相邻（曼哈顿距离为 1）=====
        Point rcA = new Point(a.currentRow(), a.currentCol());
        Point rcB = new Point(b.currentRow(), b.currentCol());
        int dist = Math.abs(rcA.x - rcB.x) + Math.abs(rcA.y - rcB.y);

        if (dist != 1) {
            // 不相邻：取消原高亮并清空选择
            a.setReady(false);
            selected = null;
            Toolkit.getDefaultToolkit().beep(); // 可选提示音
            return;
        }
        // ==============================================

        swapping = true;

        // 动画完成后的回调：做三消判定与解锁（放到 invokeLater 里更稳）
        Runnable onComplete = () -> {
            Container parent = a.getParent();              // 两个按钮在同一父容器
            if (parent instanceof JPanel) {
                SwingUtilities.invokeLater(() -> {
                    Match3Manager.removeMatches((JPanel) parent);  // 交换完成 → 判定消除
                });
            }
            swapping = false;
        };

        // 发起交换（相邻检查、动画与吸附在 ImageButton 内部完成）
        a.swapWith(b, durationMs, onComplete);

        // 真的开始交换：播放移动音效，清理高亮与选择
        if (a.isAnimating() || b.isAnimating()) {
            SoundManager.playMove();
            a.setReady(false);
            selected = null;
        } else {
            // 理论上相邻时都会进入动画；若未进入，解除节流
            swapping = false;
        }
    }
}
