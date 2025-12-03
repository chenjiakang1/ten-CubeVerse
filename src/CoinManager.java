import java.io.*;

public class CoinManager {

    private static CoinManager instance;
    private int coins;
    private final String FILE =
            System.getProperty("user.home") + File.separator + "CubeVerse_coins.txt";

    private CoinManager() {
        loadCoins();
    }

    public static synchronized CoinManager getInstance() {
        if (instance == null) {
            instance = new CoinManager();
        }
        return instance;
    }

    // 增加金币
    public synchronized void addCoins(int amount) {
        if (amount > 0) coins += amount;
        saveCoins();
    }

    // 减少金币，返回是否成功
    public synchronized boolean removeCoins(int amount) {
        if (amount > 0 && coins >= amount) {
            coins -= amount;
            saveCoins();
            return true;
        }
        return false;
    }

    public synchronized boolean isZero() {
        return coins <= 0;
    }

    public synchronized int getCoins() {
        return coins;
    }

    private void loadCoins() {
        try {
            File f = new File(FILE);
            if (!f.exists()) {
                coins = 0;
                saveCoins();
                return;
            }

            BufferedReader br = new BufferedReader(new FileReader(f));
            coins = Integer.parseInt(br.readLine().trim());
            br.close();
        } catch (Exception e) {
            coins = 0;
        }
    }

    private void saveCoins() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(FILE));
            bw.write("" + coins);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 购买炸弹：消耗2金币，成功则返回 true
    public synchronized boolean buyBomb() {
        if (coins >= 2) {
            coins -= 2;
            saveCoins();
            ItemManager.addBomb(1);
            return true;
        }
        return false;
    }

    // 购买同色消除：消耗2金币
    public synchronized boolean buyColorClear() {
        if (coins >= 2) {
            coins -= 2;
            saveCoins();
            ItemManager.addColorClear(1);
            return true;
        }
        return false;
    }

    // 购买步数+5：消耗2金币（不直接加步数！只加道具）
    public synchronized boolean buyExtraStep() {
        if (coins >= 2) {
            coins -= 2;
            saveCoins();
            ItemManager.addExtraStep(1);
            return true;
        }
        return false;
    }

}