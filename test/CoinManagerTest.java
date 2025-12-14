import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoinManagerTest {

    private CoinManager coinManager;

    @BeforeEach
    void setUp() {
        coinManager = CoinManager.getInstance();

        // 手动清零金币（模拟 reset，不改源码）
        int current = coinManager.getCoins();
        if (current > 0) {
            coinManager.removeCoins(current);
        }
    }

    @Test
    @DisplayName("CM-01: Initial coins should be zero")
    void testInitialCoins() {
        assertEquals(0, coinManager.getCoins());
    }

    @Test
    @DisplayName("CM-02: Adding coins increases coin count")
    void testAddCoins() {
        coinManager.addCoins(20);
        assertEquals(20, coinManager.getCoins());
    }

    @Test
    @DisplayName("CM-03: Removing coins decreases coin count")
    void testRemoveCoins() {
        coinManager.addCoins(20);
        boolean success = coinManager.removeCoins(5);

        assertTrue(success);
        assertEquals(15, coinManager.getCoins());
    }

    @Test
    @DisplayName("CM-04: Cannot remove more coins than owned")
    void testRemoveCoinsFail() {
        coinManager.addCoins(5);
        boolean success = coinManager.removeCoins(10);

        assertFalse(success);
        assertEquals(5, coinManager.getCoins());
    }

    @Test
    @DisplayName("CM-05: isZero returns true when coins are zero")
    void testIsZero() {
        assertTrue(coinManager.isZero());
    }
}
