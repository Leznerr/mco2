package model.util.effects;

import model.core.Character;
import model.util.GameException;
import model.util.InputValidator;
import model.util.StatusEffect;
import model.util.StatusEffectType;

/**
 * <h2>PoisonEffect</h2>
 * <p>Represents the <strong>“Poison”</strong> status condition in
 * <em>Fatal Fantasy: Tactics</em>, which deals fixed damage to a
 * {@link Character} at the start of each turn.</p>
 *
 * <p><strong>Design Principles:</strong></p>
 * <ul>
 *   <li>Immutable effect logic; turn countdown tracked internally.</li>
 *   <li>Lifecycle handled externally by the battle system.</li>
 *   <li>Zero immediate effect upon application.</li>
 * </ul>
 */
public final class PoisonEffect implements StatusEffect {

    /** Fixed poison damage per turn (non-configurable). */
    private static final int POISON_DAMAGE_PER_TURN = 5;

    /** Default number of turns poison lasts. */
    private static final int DURATION_TURNS = 3;

    /** Internal countdown for remaining effect duration. */
    private int remainingTurns;

    /**
     * Creates a new poison effect with default 3-turn duration.
     */
    public PoisonEffect() {
        this.remainingTurns = DURATION_TURNS;
    }

    /**
     * Called when the effect is first applied.
     *
     * @param target the character receiving the effect (non-null)
     * @throws GameException if {@code target} is null
     */
    @Override
    public void applyEffect(Character target) throws GameException {
        InputValidator.requireNonNull(target, "PoisonEffect target");
        // No instant effect upon application
    }

    /**
     * Deals poison damage and decrements duration at the start of the turn.
     *
     * @param target the affected character
     * @throws GameException if target is null or damage logic fails
     */
    @Override
    public void onTurnStart(Character target) throws GameException {
        InputValidator.requireNonNull(target, "PoisonEffect target");
        target.takeDamage(POISON_DAMAGE_PER_TURN);
        decrementDuration();
    }

    /**
     * No end-of-turn logic for poison.
     *
     * @param target the affected character
     */
    @Override
    public void onTurnEnd(Character target) {
        // No action on turn end
    }

    /**
     * Called when the effect is removed.
     *
     * @param target the affected character (non-null)
     * @throws GameException if {@code target} is null
     */
    @Override
    public void remove(Character target) throws GameException {
        InputValidator.requireNonNull(target, "PoisonEffect target");
        // Optional cleanup if needed
    }

    /**
     * Returns how many turns the poison will persist.
     *
     * @return remaining turns (non-negative)
     */
    @Override
    public int getDuration() {
        return remainingTurns;
    }

    /**
     * Identifies this effect as {@link StatusEffectType#POISONED}.
     *
     * @return the effect type enum
     */
    @Override
    public StatusEffectType getType() {
        return StatusEffectType.POISONED;
    }

    /**
     * Helper method to count down remaining turns.
     */
    private void decrementDuration() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    /**
     * Returns human-readable summary for tooltips/logging.
     *
     * @return formatted string
     */
    @Override
    public String toString() {
        return "Poisoned (" + remainingTurns + " turns left)";
    }
}
