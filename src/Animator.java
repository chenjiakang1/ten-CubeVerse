import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

public class Animator {
    private static final List<Runnable> tasks = new ArrayList<>();

    static {
        // 60 FPS 全局 Timer
        Timer t = new Timer(16, e -> {
            for (int i = 0; i < tasks.size(); i++) {
                tasks.get(i).run();
            }
        });
        t.start();
    }

    public static void addTask(Runnable r) {
        tasks.add(r);
    }

    public static void removeTask(Runnable r) {
        tasks.remove(r);
    }
}
