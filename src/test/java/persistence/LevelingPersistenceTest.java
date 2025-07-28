package persistence;

import model.core.*;
import model.util.GameException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Ensures new leveling fields persist. */
public class LevelingPersistenceTest {

    @AfterEach
    public void cleanup() {
        new File(Constants.SAVE_FILE_PATH).delete();
    }

    @Test
    public void testSaveLoadLevelFields() throws GameException {
        Player p = new Player("P1");
        Character c = new Character("A", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        for (int i = 0; i < 5; i++) c.incrementBattlesWon();
        c.addExperience(50);
        if (c.canLevelUp()) c.levelUp();
        p.addCharacter(c);
        GameData data = new GameData(List.of(p), List.of());
        SaveLoadService.saveGame(data);
        GameData loaded = SaveLoadService.loadGame();
        Character loadedChar = loaded.getAllPlayers().get(0).getCharacters().get(0);
        assertEquals(c.getLevel(), loadedChar.getLevel());
        assertEquals(c.getExperience(), loadedChar.getExperience());
        assertEquals(c.getBattlesWon(), loadedChar.getBattlesWon());
        assertEquals(c.getNextLevelMilestone(), loadedChar.getNextLevelMilestone());
        assertEquals(c.getUnlockedAbilitySlots(), loadedChar.getUnlockedAbilitySlots());
        assertEquals(c.getAbilitySlotCount(), loadedChar.getAbilitySlotCount());
    }
}
