package model.core;

import model.util.GameException;
import model.util.InputValidator;
import java.io.Serializable;

/**
 * Data Transfer Object (DTO) representing a single Hall of Fame leaderboard entry
 * for either a player or a character.
 *
 * <p>The entry stores the display name, total wins, accumulated XP and a
 * timestamp marking the last update.  Instances are mutable only for the win
 * and XP counters to facilitate incremental updates.</p>
 */
public final class HallOfFameEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Display name for the player or character. */
    private final String name;

    /** Total wins achieved. */
    private int wins;

    /** Total XP accumulated. */
    private int xp;

    /** Timestamp of the last update (epoch millis). */
    private long lastUpdated;

    /**
     * Constructs a new leaderboard entry.
     *
     * @param playerName the player’s display name (non-blank)
     * @param wins       initial win count (must be ≥ 0)
     * @throws GameException if parameters are invalid
     */
    public HallOfFameEntry(String name, int wins) throws GameException {
        this(name, wins, 0, System.currentTimeMillis());
    }

    /**
     * Constructs a new leaderboard entry with XP and timestamp.
     */
    public HallOfFameEntry(String name, int wins, int xp, long lastUpdated) throws GameException {
        InputValidator.requireNonBlank(name, "name");
        InputValidator.requirePositiveOrZero(wins, "wins");
        InputValidator.requirePositiveOrZero(xp, "xp");
        this.name = name;
        this.wins = wins;
        this.xp = xp;
        this.lastUpdated = lastUpdated;
    }

    /**
     * Returns the name of the player associated with this entry.
     *
     * @return non-blank player name
     */
    public String getName() {
        return name;
    }

    /** Convenience alias for backward compatibility. */
    public String getPlayerName() { return name; }

    /**
     * Returns the total number of recorded wins.
     *
     * @return number of wins (always ≥ 0)
     */
    public int getWins() { return wins; }

    /** Returns the XP value associated with this entry. */
    public int getXp() { return xp; }

    /** Returns the last update timestamp. */
    public long getLastUpdated() { return lastUpdated; }

    /**
     * Increments the total win count by one and updates the last updated timestamp
     * to the current system time.
     */
    public void incrementWins() {
        this.wins++;
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * Sets the XP value associated with this entry and updates the last updated
     * timestamp to the current system time.
     *
     * @param xp the new XP value (must be ≥ 0)
     */
    public void setXp(int xp) {
        this.xp = xp;
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * Returns a short string representation of the leaderboard entry,
     * including the name, win count, and XP.
     *
     * @return a formatted string describing the entry
     */
    @Override
    public String toString() {
        return name + " (Wins: " + wins + ", XP: " + xp + ")";
    }
}
