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

    public ImmunityEffect() {
        this.remainingTurns = DURATION_TURNS;
    }

    @Override
    public void applyEffect(Character target) throws GameException {
        InputValidator.requireNonNull(target, "ImmunityEffect target");
    }

    @Override
    public void onTurnStart(Character target) throws GameException {
        InputValidator.requireNonNull(target, "ImmunityEffect target");
        decrementDuration();
    }

    @Override
    public void onTurnEnd(Character target) {
        // no-op
    }

    @Override
    public void remove(Character target) throws GameException {
        InputValidator.requireNonNull(target, "ImmunityEffect target");
    }

    @Override
    public int getDuration() {
        return remainingTurns;
    }

    @Override
    public StatusEffectType getType() {
        return StatusEffectType.IMMUNITY;
    }

    private void decrementDuration() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    @Override
    public String toString() {
        return "Immune (" + remainingTurns + " turns left)";
    }
}
