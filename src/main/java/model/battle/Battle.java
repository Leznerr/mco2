package model.battle;

import java.util.Collections;
import java.util.List;
import model.core.Character;
import model.util.GameException;
import model.util.InputValidator;

/**
 * Domain object representing a single two-character battle session.
 *
 * <p>Tracks combatants, round progression, battle completion state, and
 * maintains a {@link CombatLog} to record combat events.</p>
 *
 * <h3>Design Notes:</h3>
 * <ul>
 *   <li><strong>Composition:</strong> Composes {@link CombatLog} for turn records.</li>
 *   <li><strong>Encapsulation:</strong> All state is private and immutable where possible.</li>
 *   <li><strong>SRP:</strong> Focuses exclusively on battle session state tracking.</li>
 * </ul>
 */
public final class Battle {

    /** Immutable list of two combatant characters. */
    private final List<Character> combatants;

    /** Current round number, starting at 1. */
    private int roundNumber = 1;

    /** Combat log recording turn-by-turn narrative. */
    private final CombatLog combatLog = new CombatLog();

    /** Flag indicating whether the battle is finished. */
    private boolean isFinished = false;

    /**
     * Constructs a battle session between two distinct characters.
     *
     * @param combatant1 first character (non-null)
     * @param combatant2 second character (non-null, must not equal {@code combatant1})
     * @throws GameException if validation fails
     */
    public Battle(Character combatant1, Character combatant2) throws GameException {
        InputValidator.requireNonNull(combatant1, "combatant1");
        InputValidator.requireNonNull(combatant2, "combatant2");

        if (combatant1 == combatant2) {
            throw new GameException("Combatants must be different instances.");
        }

        this.combatants = List.of(combatant1, combatant2);
        combatLog.addEntry("Battle started between " + combatant1.getName()
                           + " and " + combatant2.getName() + ".");
    }

    /**
     * Returns an immutable list of the two combatants.
     *
     * @return unmodifiable list of combatants
     */
    public List<Character> getCombatants() {
        return Collections.unmodifiableList(combatants);
    }

    /**
     * Returns the first combatant.
     *
     * @return first character
     */
    public Character getCharacter1() {
        return combatants.get(0);
    }

    /**
     * Returns the second combatant.
     *
     * @return second character
     */
    public Character getCharacter2() {
        return combatants.get(1);
    }

    /**
     * Returns the current round number (starting from 1).
     *
     * @return round number
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * Returns whether the battle has concluded.
     *
     * @return {@code true} if finished
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Returns the combat log containing turn-by-turn narration.
     *
     * @return {@link CombatLog} instance
     */
    public CombatLog getCombatLog() {
        return combatLog;
    }

    /**
     * Advances to the next round and logs the transition.
     *
     * @throws GameException if battle is already finished
     */
    public void nextRound() throws GameException {
        if (isFinished) {
            throw new GameException("Cannot advance rounds on a finished battle.");
        }
        roundNumber++;
        combatLog.addEntry("── Round " + roundNumber + " ──");
    }

    /**
     * Marks the battle as finished.
     *
     * @param isFinished {@code true} if battle is over
     */
    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }
}
