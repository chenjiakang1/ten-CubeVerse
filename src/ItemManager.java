public class ItemManager {
    private static int bombCount = 3;
    private static int colorClearCount = 2;
    private static int extraStepCount = 5;

    public static int getBombCount() { return bombCount; }
    public static int getColorClearCount() { return colorClearCount; }
    public static int getExtraStepCount() { return extraStepCount; }

    public static boolean useBomb() {
        if (bombCount > 0) {
            bombCount--;
            return true;
        }
        return false;
    }

    public static boolean useColorClear() {
        if (colorClearCount > 0) {
            colorClearCount--;
            return true;
        }
        return false;
    }

    public static boolean useExtraStep() {
        if (extraStepCount > 0) {
            extraStepCount--;
            return true;
        }
        return false;
    }

    public static void addBomb(int i)      { bombCount+= i; }
    public static void addColorClear(int i){ colorClearCount+= i; }
    public static void addExtraStep(int i) { extraStepCount+= i; }

    public static void reset() {
        bombCount = 3;         // 初始值可以随便改
        colorClearCount = 2;
        extraStepCount = 5;
    }

}

