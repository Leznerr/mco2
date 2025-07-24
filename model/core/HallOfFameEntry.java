package model.core;

import model.util.GameException;
import model.util.InputValidator;

/**
 * Data Transfer Object (DTO) representing a single Hall of Fame leaderboard entry.
 * 
 * <p>Stores immutable player identity and mutable win count.
 * Designed for display purposes and simple cumulative tracking.</p>
 */
public final class HallOfFameEntry {

    /** Player's display name (immutable). */
    private final String playerName;

    /** Total wins achieved by the player. */
    private int wins;

    /**
     * Constructs a new leaderboard entry.
     *
     * @param playerName the player’s display name (non-blank)
     * @param wins       initial win count (must be ≥ 0)
     * @throws GameException if parameters are invalid
     */
    public HallOfFameEntry(String playerName, int wins) throws GameException {
        InputValidator.requireNonBlank(playerName, "playerName");
        InputValidator.requirePositiveOrZero(wins, "wins");
        this.playerName = playerName;
        this.wins = wins;
    }

    /**
     * Returns the name of the player associated with this entry.
     *
     * @return non-blank player name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Returns the total number of recorded wins.
     *
     * @return number of wins (always ≥ 0)
     */
    public int getWins() {
        return wins;
    }

    /**
     * Increments the total win count by one.
     */
    public void incrementWins() {
        this.wins++;
    }

    @Override
    public String toString() {
        return playerName + " - Wins: " + wins;
    }
}
