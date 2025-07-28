package model.core;

import model.util.GameException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Tests the leveling and XP progression. */
public class LevelingTest {

    @Test
    public void testLevelUpAfterMilestone() throws GameException {
        Character c = new Character("Hero", RaceType.HUMAN, ClassType.WARRIOR, java.util.List.of());
        for (int i = 0; i < 5; i++) c.incrementBattlesWon();
        c.addExperience(50);
        assertTrue(c.canLevelUp());
        c.levelUp();
        assertEquals(2, c.getLevel());
        assertEquals(4, c.getAbilitySlots());
        assertEquals(4, c.getUnlockedAbilitySlots());
        assertEquals(10, c.getNextLevelMilestone());
    }
}
