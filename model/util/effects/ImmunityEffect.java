package model.util.effects;

import model.core.Character;
import model.util.GameException;
import model.util.InputValidator;
import model.util.StatusEffect;
import model.util.StatusEffectType;

/**
 * Status effect granting temporary immunity from negative effects or damage.
 * This implementation simply tracks duration without altering other stats.
 */
public final class ImmunityEffect implements StatusEffect {

    private static final int DURATION_TURNS = 1;
    private int remainingTurns;

    /**
     * Constructor for Immunity Effect
     */
    public ImmunityEffect() {
        this.remainingTurns = DURATION_TURNS;
    }

    /**
     * Applies the immunity effect to the target character.
     * This effect does not modify any stats immediately.
     *
     * @param target the character receiving the effect
     * @throws GameException if the target is null
     */
    @Override
    public void applyEffect(Character target) throws GameException {
        InputValidator.requireNonNull(target, "ImmunityEffect target");
    }

    /**
     * Called at the start of the target character's turn.
     * Decrements the effect's remaining duration.
     *
     * @param target the character affected by the effect
     * @throws GameException if the target is null
     */
    @Override
    public void onTurnStart(Character target) throws GameException {
        InputValidator.requireNonNull(target, "ImmunityEffect target");
        decrementDuration();
    }

    /**
     * Called at the end of the target character's turn.
     * Currently does nothing for immunity effect.
     *
     * @param target the character affected by the effect
     */
    @Override
    public void onTurnEnd(Character target) {
        // no-op
    }

    /**
     * Removes the immunity effect from the target character.
     * Currently performs validation only.
     *
     * @param target the character losing the effect
     * @throws GameException if the target is null
     */
    @Override
    public void remove(Character target) throws GameException {
        InputValidator.requireNonNull(target, "ImmunityEffect target");
    }

    /**
     * Returns the remaining duration (in turns) of the immunity effect.
     *
     * @return number of turns left
     */
    @Override
    public int getDuration() {
        return remainingTurns;
    }

    /**
     * Returns the type of this status effect.
     *
     * @return the status effect type (IMMUNITY)
     */
    @Override
    public StatusEffectType getType() {
        return StatusEffectType.IMMUNITY;
    }

    /**
     * Decrements the duration of the effect by one turn, if greater than zero.
     */
    private void decrementDuration() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    /**
     * Returns a string representation of the immunity effect,
     * including remaining turns.
     *
     * @return formatted description string
     */
    @Override
    public String toString() {
        return "Immune (" + remainingTurns + " turns left)";
    }
}
