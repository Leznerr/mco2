package model.util.effects;

import model.core.Character;
import model.util.GameException;
import model.util.InputValidator;
import model.util.StatusEffect;
import model.util.StatusEffectType;

/**
 * Temporary status effect that halves incoming damage.
 * Used for the universal Defend action and defensive abilities.
 */
public final class DefenseUpEffect implements StatusEffect {

    private static final int DEFAULT_DURATION = 1;
    private int remainingTurns;

    /**
     * Creates a defense-up effect lasting one turn.
     */
    public DefenseUpEffect() {
        this(DEFAULT_DURATION);
    }

    /**
     * Creates the effect with a custom duration.
     * 
     * @param turns number of turns the effect lasts
     */
    public DefenseUpEffect(int turns) {
        this.remainingTurns = Math.max(1, turns);
    }

    /**
     * Applies the effect to the target character.
     * This method does not alter the character state immediately,
     * as the effect is passive until damage is received.
     *
     * @param target the character receiving the effect
     * @throws GameException if the target is null
     */
    @Override
    public void applyEffect(Character target) throws GameException {
        InputValidator.requireNonNull(target, "DefenseUpEffect target");
        // no immediate change to character state
    }

    /**
     * Called at the start of the character's turn.
     * Decrements the remaining duration of the effect.
     *
     * @param target the character affected by this effect
     * @throws GameException if the target is null
     */
    @Override
    public void onTurnStart(Character target) throws GameException {
        InputValidator.requireNonNull(target, "DefenseUpEffect target");
        decrement();
    }


    /**
     * Called at the end of the character's turn.
     * This effect does not perform any action at turn end.
     *
     * @param target the character affected by this effect
     * @throws GameException if the target is null
     */
    @Override
    public void onTurnEnd(Character target) throws GameException {
        InputValidator.requireNonNull(target, "DefenseUpEffect target");
        // no behaviour
    }

    /**
     * Removes the effect from the character.
     * No cleanup is needed for this particular effect.
     *
     * @param target the character from which the effect is removed
     */
    @Override
    public void remove(Character target) {
        // nothing to reset
    }

    /**
     * @return the number of turns the effect will remain active
     */
    @Override
    public int getDuration() {
        return remainingTurns;
    }

    /**
     * @return the specific type of this status effect
     */
    @Override
    public StatusEffectType getType() {
        return StatusEffectType.DEFENSE_UP;
    }

    /**
     * Decreases the remaining duration by one, to a minimum of zero.
     */
    private void decrement() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    /**
     * Returns a string representation of the effect and its duration.
     *
     * @return user-friendly status string
     */
    @Override
    public String toString() {
        return "Defending (" + remainingTurns + " turns left)";
    }
}
