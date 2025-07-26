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

public class RewardSystemTest {

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
    public void testItemAwardEveryThreeWins() throws Exception {
        Player p = new Player("P");
        Character c = new Character("C", RaceType.HUMAN, ClassType.WARRIOR, List.of());
        p.addCharacter(c);
        SaveLoadService.saveGame(new GameData(List.of(p), List.of()));

        GameManagerController controller = buildController();
        Player loaded = controller.getPlayers().get(0);
        Character loadedChar = loaded.getCharacters().get(0);

        controller.handlePlayerWin(loaded, loadedChar);
        controller.handlePlayerWin(loaded, loadedChar);
        assertEquals(0, loadedChar.getInventory().getAllItems().size());
        controller.handlePlayerWin(loaded, loadedChar);
        assertEquals(1, loadedChar.getInventory().getAllItems().size());
        controller.handlePlayerWin(loaded, loadedChar);
        controller.handlePlayerWin(loaded, loadedChar);
        assertEquals(1, loadedChar.getInventory().getAllItems().size());
        controller.handlePlayerWin(loaded, loadedChar);
        assertEquals(2, loadedChar.getInventory().getAllItems().size());
    }
}
