import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ItemManagerTest {

    @BeforeEach
    void setUp() {
        // 每个测试前都恢复到初始数量：bomb=3, color=2, step=5
        ItemManager.reset();
    }

    // ---------- Initial State ----------

    @Test
    @DisplayName("IT-01: Initial item counts should match default values")
    void testInitialCounts() {
        assertEquals(3, ItemManager.getBombCount());
        assertEquals(2, ItemManager.getColorClearCount());
        assertEquals(5, ItemManager.getExtraStepCount());
    }

    // ---------- Bomb Item ----------

    @Test
    @DisplayName("IT-02: Add bomb increases bomb count")
    void testAddBomb() {
        ItemManager.addBomb(1);
        assertEquals(4, ItemManager.getBombCount()); // 3 + 1
    }

    @Test
    @DisplayName("IT-03: Use bomb decreases bomb count")
    void testUseBomb() {
        boolean success = ItemManager.useBomb();

        assertTrue(success);
        assertEquals(2, ItemManager.getBombCount()); // 3 - 1
    }

    @Test
    @DisplayName("IT-04: Cannot use bomb when count reaches zero")
    void testUseBombWhenEmpty() {
        // 先把 3 个炸弹全部用掉
        assertTrue(ItemManager.useBomb());
        assertTrue(ItemManager.useBomb());
        assertTrue(ItemManager.useBomb());
        assertEquals(0, ItemManager.getBombCount());

        // 再用一次应该失败，数量保持为 0
        boolean success = ItemManager.useBomb();
        assertFalse(success);
        assertEquals(0, ItemManager.getBombCount());
    }

    // ---------- Color Clear Item ----------

    @Test
    @DisplayName("IT-05: Add color clear increases count")
    void testAddColorClear() {
        ItemManager.addColorClear(1);
        assertEquals(3, ItemManager.getColorClearCount()); // 2 + 1
    }

    @Test
    @DisplayName("IT-06: Use color clear decreases count")
    void testUseColorClear() {
        boolean success = ItemManager.useColorClear();

        assertTrue(success);
        assertEquals(1, ItemManager.getColorClearCount()); // 2 - 1
    }

    // ---------- Extra Step Item ----------

    @Test
    @DisplayName("IT-07: Add extra step item increases count")
    void testAddExtraStep() {
        ItemManager.addExtraStep(1);
        assertEquals(6, ItemManager.getExtraStepCount()); // 5 + 1
    }

    @Test
    @DisplayName("IT-08: Use extra step decreases count")
    void testUseExtraStep() {
        boolean success = ItemManager.useExtraStep();

        assertTrue(success);
        assertEquals(4, ItemManager.getExtraStepCount()); // 5 - 1
    }
}
