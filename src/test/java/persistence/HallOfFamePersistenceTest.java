package persistence;

import model.core.HallOfFameEntry;
import model.util.GameException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests serialization of HallOfFameData. */
public class HallOfFamePersistenceTest {

    @AfterEach
    public void cleanup() {
        new File("save/hall_of_fame.dat").delete();
    }

    @Test
    public void testSaveAndLoadHallOfFame() throws GameException {
        HallOfFameEntry p = new HallOfFameEntry("Alice", 2, 10, 1L);
        HallOfFameEntry c = new HallOfFameEntry("Hero", 3, 20, 1L);
        HallOfFameData data = new HallOfFameData(List.of(p), List.of(c));

        SaveLoadService.saveHallOfFame(data);
        HallOfFameData loaded = SaveLoadService.loadHallOfFame();

        assertEquals(1, loaded.getPlayers().size());
        assertEquals(1, loaded.getCharacters().size());
        assertEquals("Hero", loaded.getCharacters().get(0).getName());
    }
}
