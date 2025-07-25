package persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.core.Player;
import model.core.HallOfFameEntry;
import model.util.GameException;
import model.util.InputValidator;

/**
 * DTO that aggregates *all* persistent data required by
 * <em>Fatal Fantasy&nbsp;: Tactics</em>.
 *
 * <p>The object is <strong>mutable but defensively copied</strong>:
 * callers receive unmodifiable views so internal state cannot be
 * altered unintentionally.</p>
 *
 * <p>Implements {@link Serializable} so it can be saved as a binary
 * stream, JSON, or any format chosen by {@code SaveLoadService}.</p>
 */
public class GameData implements Serializable {

    private static final long serialVersionUID = 1L;

    /* ------------------------------------------------------------------ */
    /* Fields (UML-specified)                                             */
    /* ------------------------------------------------------------------ */

    private List<Player>          allPlayers;
    private List<HallOfFameEntry> hallOfFame;

    /* ------------------------------------------------------------------ */
    /* Constructors                                                       */
    /* ------------------------------------------------------------------ */

    /** Creates an empty container (no players, no HoF). */
    public GameData() {
        this.allPlayers = new ArrayList<>();
        this.hallOfFame = new ArrayList<>();
    }

    /**
     * Full constructor.
     *
     * @param allPlayers  list of players (defensively copied, non-null)
     * @param hallOfFame  list of hall-of-fame entries (non-null)
     * @throws GameException if any argument is {@code null}
     */
    public GameData(List<Player> allPlayers,
                    List<HallOfFameEntry> hallOfFame) throws GameException {

        setAllPlayers(allPlayers);
        setHallOfFame(hallOfFame);
    }

    /* ------------------------------------------------------------------ */
    /* Getters (return unmodifiable views)                                */
    /* ------------------------------------------------------------------ */

    public List<Player> getAllPlayers() {
        return Collections.unmodifiableList(allPlayers);
    }

    public List<HallOfFameEntry> getHallOfFame() {
        return Collections.unmodifiableList(hallOfFame);
    }

    /* ------------------------------------------------------------------ */
    /* Setters (defensive copy)                                           */
    /* ------------------------------------------------------------------ */

    public void setAllPlayers(List<Player> players) throws GameException {
        InputValidator.requireNonNull(players, "allPlayers");
        this.allPlayers = new ArrayList<>(players);
    }

    public void setHallOfFame(List<HallOfFameEntry> entries) throws GameException {
        InputValidator.requireNonNull(entries, "hallOfFame");
        this.hallOfFame = new ArrayList<>(entries);
    }
}
