package model.util.effects;

import model.core.Character;
import model.util.GameException;
import model.util.InputValidator;
import model.util.StatusEffect;
import model.util.StatusEffectType;

/**
 * Simple status effect that represents a temporary evasion boost.
 * Currently it has no direct gameplay impact other than being tracked
 * in a character's active status list.
 */
public final class EvadeEffect implements StatusEffect {

    private static final int DURATION_TURNS = 1;

    private int remainingTurns;

    public EvadeEffect() {
        this.remainingTurns = DURATION_TURNS;
    }

    @Override
    public void applyEffect(Character target) throws GameException {
        InputValidator.requireNonNull(target, "EvadeEffect target");
        // No immediate stat change
    }

    @Override
    public void onTurnStart(Character target) throws GameException {
        InputValidator.requireNonNull(target, "EvadeEffect target");
        decrementDuration();
    }

    @Override
    public void onTurnEnd(Character target) {
        // No end of turn behaviour
    }

    @Override
    public void remove(Character target) throws GameException {
        InputValidator.requireNonNull(target, "EvadeEffect target");
    }

    @Override
    public int getDuration() {
        return remainingTurns;
    }

    @Override
    public StatusEffectType getType() {
        return StatusEffectType.EVADING;
    }

    private void decrementDuration() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    @Override
    public String toString() {
        return "Evading (" + remainingTurns + " turns left)";
    }
}
