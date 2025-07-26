package persistence;

import model.core.HallOfFameEntry;
import model.util.GameException;
import model.util.InputValidator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Container object for Hall of Fame persistence. Holds separate
 * leaderboards for players and characters.
 */
public class HallOfFameData implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<HallOfFameEntry> players;
    private List<HallOfFameEntry> characters;

    /** Creates an empty Hall of Fame container. */
    public HallOfFameData() {
        players = new ArrayList<>();
        characters = new ArrayList<>();
    }

    /**
     * Full constructor using defensive copies.
     */
    public HallOfFameData(List<HallOfFameEntry> players,
                          List<HallOfFameEntry> characters) throws GameException {
        setPlayers(players);
        setCharacters(characters);
    }

    public List<HallOfFameEntry> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public List<HallOfFameEntry> getCharacters() {
        return Collections.unmodifiableList(characters);
    }

    public void setPlayers(List<HallOfFameEntry> list) throws GameException {
        InputValidator.requireNonNull(list, "players");
        players = new ArrayList<>(list);
    }

    public void setCharacters(List<HallOfFameEntry> list) throws GameException {
        InputValidator.requireNonNull(list, "characters");
        characters = new ArrayList<>(list);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (players == null) players = new ArrayList<>();
        if (characters == null) characters = new ArrayList<>();
    }
}
