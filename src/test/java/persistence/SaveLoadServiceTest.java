package persistence;

import model.core.ClassType;
import model.core.Character;
import model.core.Player;
import model.core.RaceType;
import model.util.GameException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests basic save and load functionality */
public class SaveLoadServiceTest {

    @AfterEach
    public void cleanup() {
        new File("game_data.dat").delete();
    }

    @Test
    public void testSaveAndLoadGame() throws GameException {
        Player p = new Player("P1");
        Character c = new Character("A", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        p.addCharacter(c);
        GameData data = new GameData(List.of(p), List.of());

        SaveLoadService.saveGame(data);
        GameData loaded = SaveLoadService.loadGame();

        assertEquals(1, loaded.getAllPlayers().size());
    }
}
