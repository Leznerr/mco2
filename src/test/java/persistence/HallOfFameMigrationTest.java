package persistence;

import model.core.HallOfFameEntry;
import model.util.GameException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Verifies legacy Hall of Fame data is loaded without errors. */
public class HallOfFameMigrationTest {

    @AfterEach
    public void cleanup() {
        new java.io.File("save/hall_of_fame.dat").delete();
    }

    @Test
    public void testLegacyListUpgrade() throws Exception {
        HallOfFameEntry entry = new HallOfFameEntry("Legacy", 5, 42, 1L);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("save/hall_of_fame.dat"))) {
            out.writeObject(List.of(entry));
        }

        HallOfFameData data = SaveLoadService.loadHallOfFame();
        assertEquals(1, data.getPlayers().size());
        assertEquals("Legacy", data.getPlayers().get(0).getName());
    }
}
