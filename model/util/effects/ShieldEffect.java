package model.util.effects;

import model.core.Character;
import model.util.GameException;
import model.util.InputValidator;
import model.util.StatusEffect;
import model.util.StatusEffectType;

/**
 * Blocks a fixed amount of damage from the next incoming attack.
 */
public final class ShieldEffect implements StatusEffect {

    private static final int BLOCK_AMOUNT = 15;
    private boolean used = false;

    @Override
    public void applyEffect(Character target) throws GameException {
        InputValidator.requireNonNull(target, "ShieldEffect target");
    }

    @Override
    public void onTurnStart(Character target) {
        // no duration decrement; lasts until used once
    }

    @Override
    public void onTurnEnd(Character target) {
        // no-op
    }

    @Override
    public void remove(Character target) {
        used = true;
    }

    @Override
    public int getDuration() {
        return used ? 0 : 1;
    }

    @Override
    public StatusEffectType getType() {
        return StatusEffectType.SHIELDED;
    }

    public int absorb(int damage) {
        if (used) return damage;
        int remaining = Math.max(0, damage - BLOCK_AMOUNT);
        used = true;
        return remaining;
    }
}
