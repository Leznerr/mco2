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

    /** Creates a defense-up effect lasting one turn. */
    public DefenseUpEffect() {
        this(DEFAULT_DURATION);
    }

    /**
     * Creates the effect with a custom duration.
     * @param turns number of turns the effect lasts
     */
    public DefenseUpEffect(int turns) {
        this.remainingTurns = Math.max(1, turns);
    }

    @Override
    public void applyEffect(Character target) throws GameException {
        InputValidator.requireNonNull(target, "DefenseUpEffect target");
        // no immediate change to character state
    }

    @Override
    public void onTurnStart(Character target) throws GameException {
        InputValidator.requireNonNull(target, "DefenseUpEffect target");
        decrement();
    }

    @Override
    public void onTurnEnd(Character target) throws GameException {
        InputValidator.requireNonNull(target, "DefenseUpEffect target");
        // no behaviour
    }

    @Override
    public void remove(Character target) {
        // nothing to reset
    }

    @Override
    public int getDuration() {
        return remainingTurns;
    }

    @Override
    public StatusEffectType getType() {
        return StatusEffectType.DEFENSE_UP;
    }

    private void decrement() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    @Override
    public String toString() {
        return "Defending (" + remainingTurns + " turns left)";
    }
}
