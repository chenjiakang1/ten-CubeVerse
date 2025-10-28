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
        ImageButton other = btn;

        // 再点自己：取消准备
        if (other == selected) {
            selected.setReady(false);
            selected = null;
            return;
        }

        // 若任一处于动画中，忽略
        if (selected.isAnimating() || other.isAnimating()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        swapping = true;

        ImageButton a = selected;
        ImageButton b = other;

        // 动画完成后的回调：做三消判定与解锁
        Runnable onComplete = () -> {
            Container parent = a.getParent();
            if (parent instanceof JPanel) {
                Match3Manager.removeMatches((JPanel) parent);
            }
            swapping = false;
        };

        // 发起交换（相邻检查、动画与吸附在 ImageButton 内部完成）
        a.swapWith(b, durationMs, onComplete);

        // 取消高亮并清理选择状态
        a.setReady(false);
        selected = null;

        // 如果动画真的启动了（相邻且通过校验），播放移动音效；
        // 否则解除节流（不相邻时 swapWith 会立即返回且不会置 animating）
        if (a.isAnimating() || b.isAnimating()) {
            SoundManager.playMove();
        } else {
            swapping = false;
        }
    }
}
