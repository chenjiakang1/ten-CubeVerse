public class CoinManager {

    private static CoinManager instance;
    private int coins;

    private CoinManager() {
        coins = 0;
    }

    // 获取单例实例
    public static synchronized CoinManager getInstance() {
        if (instance == null) {
            instance = new CoinManager();
        }
        return instance;
    }

    // 增加金币
    public synchronized void addCoins(int amount) {
        if (amount > 0) coins += amount;
    }

    // 减少金币，返回是否成功
    public synchronized boolean removeCoins(int amount) {
        if (amount > 0 && coins >= amount) {
            coins -= amount;
            return true;
        }
        return false;
    }

    // 判断金币是否为零
    public synchronized boolean isZero() {
        return coins <= 0;
    }

    // 获取当前金币数量
    public synchronized int getCoins() {
        return coins;
    }
}
