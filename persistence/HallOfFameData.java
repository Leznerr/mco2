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

    /**
     * Constructs an empty Hall of Fame container
     * with no player or character entries.
     */
    public HallOfFameData() {
        players = new ArrayList<>();
        characters = new ArrayList<>();
    }

    /**
     * Full constructor that accepts initial leaderboards
     * for players and characters.
     *
     * @param players    the player leaderboard entries (non-null)
     * @param characters the character leaderboard entries (non-null)
     * @throws GameException if any argument is {@code null}
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

    /**
     * Ensures collections are initialized properly
     * upon deserialization, even if missing.
     *
     * @param in the object input stream
     * @throws java.io.IOException if an I/O error occurs
     * @throws ClassNotFoundException if class resolution fails
     */
    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (players == null) players = new ArrayList<>();
        if (characters == null) characters = new ArrayList<>();
    }
}
