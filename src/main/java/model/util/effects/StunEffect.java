package model.util.effects;

import model.core.Character;
import model.util.GameException;
import model.util.InputValidator;
import model.util.StatusEffect;
import model.util.StatusEffectType;

/**
 * <h2>StunEffect</h2>
 * <p>Represents the <strong>“Stun”</strong> status in
 * <em>Fatal Fantasy: Tactics</em>, which blocks a character's actions
 * for a fixed number of turns.</p>
 *
 * <p><strong>Effect Behavior:</strong></p>
 * <ul>
 *   <li>Applies immediate incapacitation via {@code setStunned(true)}.</li>
 *   <li>Lasts 2 turns unless removed prematurely.</li>
 *   <li>Automatically clears stun flag when effect expires.</li>
 * </ul>
 *
 * <p><strong>Design Principles:</strong></p>
 * <ul>
 *   <li>Immutable effect logic; countdown tracked internally.</li>
 *   <li>Zero turn-end action; only start-of-turn management.</li>
 *   <li>Complies with MVC and clean separation of concerns.</li>
 * </ul>
 */
public final class StunEffect implements StatusEffect {

    /** Default number of turns stun lasts. */
    private static final int DURATION_TURNS = 2;

    /** Remaining turns for which the target is stunned. */
    private int remainingTurns;

    /**
     * Constructs a new {@code StunEffect} with 2-turn duration.
     */
    public StunEffect() {
        this.remainingTurns = DURATION_TURNS;
    }

    /**
     * Immediately applies the stunned state.
     *
     * @param target the affected character (non-null)
     * @throws GameException if {@code target} is null
     */
    @Override
    public void applyEffect(Character target) throws GameException {
        InputValidator.requireNonNull(target, "StunEffect target");
        target.setStunned(true);
    }

    /**
     * Decrements stun duration and clears effect if expired.
     *
     * @param target the stunned character (non-null)
     * @throws GameException if {@code target} is null
     */
    @Override
    public void onTurnStart(Character target) throws GameException {
        InputValidator.requireNonNull(target, "StunEffect target");
        decrementDuration();
        if (remainingTurns == 0) {
            target.setStunned(false);
        }
    }

    /**
     * No action required at the end of the turn.
     *
     * @param target the affected character
     */
    @Override
    public void onTurnEnd(Character target) {
        // No-op
    }

    /**
     * Removes the stun state prematurely.
     *
     * @param target the stunned character (non-null)
     * @throws GameException if {@code target} is null
     */
    @Override
    public void remove(Character target) throws GameException {
        InputValidator.requireNonNull(target, "StunEffect target");
        target.setStunned(false);
    }

    /**
     * Returns number of turns left for stun effect.
     *
     * @return remaining turn count
     */
    @Override
    public int getDuration() {
        return remainingTurns;
    }

    /**
     * Returns the effect's type enum.
     *
     * @return {@code StatusEffectType.STUNNED}
     */
    @Override
    public StatusEffectType getType() {
        return StatusEffectType.STUNNED;
    }

    /**
     * Decrements internal turn counter.
     */
    private void decrementDuration() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    /**
     * Tooltip / logging helper method.
     *
     * @return human-readable stun state
     */
    @Override
    public String toString() {
        return "Stunned (" + remainingTurns + " turns left)";
    }
}
