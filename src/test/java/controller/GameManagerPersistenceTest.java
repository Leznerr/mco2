package controller;

import model.core.Player;
import model.util.Constants;
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

/** Tests that GameManagerController persists player data. */
public class GameManagerPersistenceTest {

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

        // Dispose the frame to avoid GUI leftovers
        Field stageField = SceneManager.class.getDeclaredField("stage");
        stageField.setAccessible(true);
        JFrame stage = (JFrame) stageField.get(sm);
        stage.dispose();

        return controller;
    }

    @Test
    public void testPlayersLoadedFromSaveOnStartup() throws Exception {
        Player p1 = new Player("Alice");
        Player p2 = new Player("Bob");
        SaveLoadService.saveGame(new GameData(List.of(p1, p2), List.of()));

        GameManagerController controller = buildController();
        assertEquals(2, controller.getPlayers().size());
    }

    @Test
    public void testRegisterPlayersPersistsData() throws Exception {
        GameManagerController controller = buildController();
        assertTrue(controller.handleRegisterPlayers("Anna", "Ben"));

        GameManagerController controller2 = buildController();
        assertEquals(2, controller2.getPlayers().size());
    }
}
