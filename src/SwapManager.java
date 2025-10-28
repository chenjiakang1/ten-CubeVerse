import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwapManager implements ActionListener {
    private ImageButton selected = null;   // 当前“准备/高亮”的按钮
    private boolean swapping = false;      // 交换动画进行中
    private final int durationMs;          // 动画时长
    private long lastSwapEnd = 0L;         // 上次交换结束时间戳（用于冷却）
    private static final long COOLDOWN_MS = 120; // 交换结束后的冷却期

    public SwapManager(int durationMs) {
        this.durationMs = durationMs;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        handleClick((ImageButton) e.getSource());
    }

    public void handleClick(ImageButton btn) {
        long now = System.currentTimeMillis();

        // 动画中或刚结束冷却期内：直接忽略
        if (swapping || (now - lastSwapEnd) < COOLDOWN_MS) return;

        if (selected == null) {
            // 第一次点击 → 进入准备状态
            selected = btn;
            selected.setReady(true);
            return;
        }

        if (selected == btn) {
            // 再点同一个 → 取消准备
            selected.setReady(false);
            selected = null;
            return;
        }

        // 点击另一个 → 执行交换
        beginSwap(selected, btn);
    }

    private void beginSwap(ImageButton a, ImageButton b) {
        // 交换开始立刻清空“准备”，避免竞态
        if (selected != null) {
            selected.setReady(false);
            selected = null;
        }
        a.setReady(false);
        b.setReady(false);

        swapping = true;
        a.setEnabled(false);
        b.setEnabled(false);

        a.swapWith(b, durationMs);

        // 动画结束后恢复
        Timer done = new Timer(durationMs, evt -> {
            a.setEnabled(true);
            b.setEnabled(true);
            swapping = false;
            lastSwapEnd = System.currentTimeMillis();
            ((Timer) evt.getSource()).stop();
        });
        done.setRepeats(false);
        done.start();
    }

    public void reset() {
        if (selected != null) selected.setReady(false);
        selected = null;
        swapping = false;
        lastSwapEnd = 0L;
    }
}
