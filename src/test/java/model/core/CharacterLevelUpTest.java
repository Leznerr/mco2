import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import model.core.Character;
import model.core.RaceType;
import model.core.ClassType;

public class CharacterLevelUpTest {

    @Test
    void levelUpFromOneToThreeUnlocksSlot() throws Exception {
        Character c = new Character("Hero", RaceType.HUMAN, ClassType.MAGE);
        c.addExperience(250); // enough for level 3
        for (int i = 0; i < 5; i++) {
            c.incrementBattlesWon();
        }
        assertTrue(c.canLevelUp());
        c.levelUp();
        assertEquals(3, c.getLevel());
        assertEquals(4, c.getAbilitySlotCount());
    }

    @Test
    void levelUpFromThreeToFiveUnlocksSlot() throws Exception {
        Character c = new Character("Hero", RaceType.HUMAN, ClassType.MAGE);
        c.addExperience(250); // first reach level 3
        for (int i = 0; i < 5; i++) {
            c.incrementBattlesWon();
        }
        c.levelUp();
        assertEquals(3, c.getLevel());
        assertEquals(4, c.getAbilitySlotCount());

        c.addExperience(450); // total 700 -> level 5
        for (int i = 0; i < 10; i++) {
            c.incrementBattlesWon();
        }
        assertTrue(c.canLevelUp());
        c.levelUp();
        assertEquals(5, c.getLevel());
        assertEquals(5, c.getAbilitySlotCount());
    }
}
