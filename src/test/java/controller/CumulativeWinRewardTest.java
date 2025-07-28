package controller;

import model.core.ClassType;
import model.core.Character;
import model.core.Player;
import model.core.RaceType;
import model.util.Constants;
import model.util.GameException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.GameData;
import persistence.SaveLoadService;

import javax.swing.JFrame;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CumulativeWinRewardTest {
    @BeforeEach
    @AfterEach
    public void clean() {
        new File(Constants.SAVE_FILE_PATH).delete();
    }

    private GameManagerController buildController() throws Exception {
        System.setProperty("java.awt.headless", "true");
        SceneManager sm = new SceneManager();
        Field f = SceneManager.class.getDeclaredField("gameManagerController");
        f.setAccessible(true);
        GameManagerController controller = (GameManagerController) f.get(sm);
        Field stageField = SceneManager.class.getDeclaredField("stage");
        stageField.setAccessible(true);
        JFrame stage = (JFrame) stageField.get(sm);
        stage.dispose();
        return controller;
    }

    @Test
    public void testRewardAfterThreeCumulativeWins() throws Exception {
        Player p = new Player("P");
        Character c1 = new Character("C1", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        Character c2 = new Character("C2", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        p.addCharacter(c1);
        p.addCharacter(c2);
        SaveLoadService.saveGame(new GameData(List.of(p), List.of()));

        GameManagerController controller = buildController();
        Player loaded = controller.getPlayers().get(0);
        Character lc1 = loaded.getCharacters().get(0);
        Character lc2 = loaded.getCharacters().get(1);

        controller.handlePlayerWin(loaded, lc1); // win 1
        controller.handlePlayerWin(loaded, lc2); // win 2
        assertEquals(0, lc1.getInventory().getAllItems().size());
        assertEquals(0, lc2.getInventory().getAllItems().size());
        controller.handlePlayerWin(loaded, lc1); // win 3 triggers reward
        assertEquals(1, lc1.getInventory().getAllItems().size());
    }
}
