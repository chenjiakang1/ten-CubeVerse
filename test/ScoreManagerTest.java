import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScoreManagerTest {

    @BeforeEach
    void setUp() {
        ScoreManager.reset();
    }

    @Test
    @DisplayName("TC-01: Initial score should be zero")
    void testInitialScore() {
        assertEquals(0, ScoreManager.getScore());
    }

    @Test
    @DisplayName("TC-02: Add positive score")
    void testAddPositiveScore() {
        ScoreManager.addScore(10);
        assertEquals(10, ScoreManager.getScore());
    }

    @Test
    @DisplayName("TC-03: Add score multiple times")
    void testAddScoreMultipleTimes() {
        ScoreManager.addScore(10);
        ScoreManager.addScore(20);
        assertEquals(30, ScoreManager.getScore());
    }

    @Test
    @DisplayName("TC-04: Boundary value - add zero")
    void testAddZeroScore() {
        ScoreManager.addScore(0);
        assertEquals(0, ScoreManager.getScore());
    }

    @Test
    @DisplayName("TC-05: Exceptional case - negative input")
    void testAddNegativeScore() {
        ScoreManager.addScore(-10);
        assertEquals(0, ScoreManager.getScore());
    }
}
