package model.util.effects;

import model.core.Character;
import model.util.GameException;
import model.util.InputValidator;
import model.util.StatusEffect;
import model.util.StatusEffectType;

/**
 * Target takes additional damage while marked.
 */
public final class MarkedEffect implements StatusEffect {

    private static final int DURATION = 2;
    private int remaining = DURATION;

    @Override
    public void applyEffect(Character target) throws GameException {
        InputValidator.requireNonNull(target, "MarkedEffect target");
    }

    @Override
    public void onTurnStart(Character target) throws GameException {
        InputValidator.requireNonNull(target, "MarkedEffect target");
        if (remaining > 0) remaining--;
    }

    @Override
    public void onTurnEnd(Character target) {
        // no-op
    }

    @Override
    public void remove(Character target) {}

    @Override
    public int getDuration() {
        return remaining;
    }

    @Override
    public StatusEffectType getType() {
        return StatusEffectType.MARKED;
    }
}
