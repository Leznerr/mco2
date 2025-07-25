package model.core;

import model.util.GameException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private Character newChar(String name) throws GameException {
        return new Character(name, RaceType.HUMAN, ClassType.MAGE);
    }

    @Test
    public void constructorRejectsBlankName() {
        assertThrows(GameException.class, () -> new Player(" "));
    }

    @Test
    public void addCharacterAddsToRoster() throws GameException {
        Player p = new Player("Alice");
        p.addCharacter(newChar("hero"));
        assertEquals(1, p.getCharacters().size());
    }

    @Test
    public void addCharacterRejectsDuplicateName() throws Exception {
        Player p = new Player("Bob");
        p.addCharacter(newChar("dup"));
        assertThrows(GameException.class, () -> p.addCharacter(newChar("dup")));
    }

    @Test
    public void removeCharacterReturnsTrueWhenRemoved() throws Exception {
        Player p = new Player("Carl");
        p.addCharacter(newChar("tmp"));
        assertTrue(p.removeCharacter("tmp"));
        assertEquals(0, p.getCharacters().size());
    }
}
