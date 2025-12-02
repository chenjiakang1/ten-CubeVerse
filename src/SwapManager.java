package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwapManager implements ActionListener {
    private final int durationMs;     // 动画时长
    private final MainWindow window;  // 🔥 引入 MainWindow 用于获取布局参数
    private ImageButton selected = null;
    private boolean swapping = false; // 动画期间节流

    public SwapManager(MainWindow window, int durationMs) {
        this.window = window;                   // ★ 保存引用
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

        // ===== 显式检查是否相邻 =====
        Point rcA = new Point(a.currentRow(), a.currentCol());
        Point rcB = new Point(b.currentRow(), b.currentCol());
        int dist = Math.abs(rcA.x - rcB.x) + Math.abs(rcA.y - rcB.y);
        if (dist != 1) {
            a.setReady(false);
            selected = null;
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        // ==========================

        swapping = true;

        // 动画完成后的回调：做三消判定与解锁
        Runnable onComplete = () -> {
            Container parent = a.getParent();
            if (parent instanceof JPanel) {
                SwingUtilities.invokeLater(() -> {
                    Match3Manager.removeMatches(
                            (JPanel) parent,
                            window.getCellW(),   // ★ 来自 getter
                            window.getCellH(),
                            window.getOriginX(),
                            window.getOriginY(),
                            window.getCols(),
                            window.getHGap(),
                            window.getVGap()
                    );
                });
            }

            window.decreaseStep();   // 每次交换后扣 1 步

            swapping = false;
        };

        // 发起交换
        a.swapWith(b, durationMs, onComplete);

        // 开始交换：播放音效/清理状态
        if (a.isAnimating() || b.isAnimating()) {
            SoundManager.playMove();
            a.setReady(false);
            selected = null;
        } else {
            swapping = false;
        }
    }
}
