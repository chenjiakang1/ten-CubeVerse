import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwapManager implements ActionListener {

    private final int durationMs;
    private final MainWindow window;  // 用于读取 currentItem / cellW / hGap 等
    private ImageButton selected = null;
    private boolean swapping = false;

    public SwapManager(MainWindow window, int durationMs) {
        this.window = window;
        this.durationMs = Math.max(0, durationMs);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (swapping) return;
        if (!(e.getSource() instanceof ImageButton)) return;

        ImageButton btn = (ImageButton) e.getSource();

        // =======================================================
        //   ★★ 1. 道具模式优先处理（比交换逻辑优先级高） ★★
        // =======================================================
        ItemType item = window.getCurrentItem();

        if (item != ItemType.NONE) {
            handleItemClick(btn, item);
            return;   // 道具使用 → 不走交换逻辑
        }


        // =======================================================
        //   ★★ 2. 普通交换逻辑（原版代码） ★★
        // =======================================================

        // 第一次点击：进入准备状态
        if (selected == null) {
            selected = btn;
            selected.setReady(true);
            return;
        }

        ImageButton a = selected;
        ImageButton b = btn;

        // 再点自己：取消
        if (a == b) {
            a.setReady(false);
            selected = null;
            return;
        }

        // 如果处于动画中：禁止点击
        if (a.isAnimating() || b.isAnimating()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        // ===== 检查是否相邻 =====
        Point rcA = new Point(a.currentRow(), a.currentCol());
        Point rcB = new Point(b.currentRow(), b.currentCol());
        int dist = Math.abs(rcA.x - rcB.x) + Math.abs(rcA.y - rcB.y);

        if (dist != 1) {
            a.setReady(false);
            selected = null;
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        swapping = true;

        // 动画后的回调：三消 + 扣步数
        Runnable onComplete = () -> {
            Container parent = a.getParent();
            if (parent instanceof JPanel) {
                SwingUtilities.invokeLater(() -> {
                    Match3Manager.removeMatches(
                            (JPanel) parent,
                            window.getCellW(),
                            window.getCellH(),
                            window.getOriginX(),
                            window.getOriginY(),
                            window.getCols(),
                            window.getHGap(),
                            window.getVGap()
                    );
                });
            }

            window.decreaseStep();   // 交换成功 → 扣步数

            swapping = false;
        };

        // 发起交换动画
        a.swapWith(b, durationMs, onComplete);

        // 播放移动音效 + 清理选中状态
        if (a.isAnimating() || b.isAnimating()) {
            SoundManager.playMove();
            a.setReady(false);
            selected = null;
        } else {
            swapping = false;
        }
    }



    // =======================================================
    //   ★★ 道具点击逻辑 ★★
    // =======================================================
    private void handleItemClick(ImageButton btn, ItemType item) {

        switch (item) {

            case BOMB:
                Match3Manager.useBomb(btn);
                break;

            case COLOR_CLEAR:
                Match3Manager.useColorClear(btn);
                break;

            case EXTRA_STEP:
                // 不需要点棋盘，可由 MainWindow 按钮直接处理
                break;

            default:
                break;
        }

        // 道具用完 → 重置回正常模式
        window.clearItem();
    }
}
