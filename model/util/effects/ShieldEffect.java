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

    /**
     * Applies the shield effect to the target character.
     *
     * @param target the character receiving the effect
     * @throws GameException if the target is null
     */
    @Override
    public void applyEffect(Character target) throws GameException {
        InputValidator.requireNonNull(target, "ShieldEffect target");
    }

    /**
     * Called at the start of the target's turn.
     * This effect does not decrement over time and remains until used.
     *
     * @param target the character affected by this status
     */
    @Override
    public void onTurnStart(Character target) {
        // no duration decrement; lasts until used once
    }

    /**
     * Called at the end of the target's turn.
     * ShieldEffect does not take action on turn end.
     *
     * @param target the character affected by this status
     */
    @Override
    public void onTurnEnd(Character target) {
        // no-op
    }

    /**
     * Removes the effect from the target character,
     * marking the shield as used.
     *
     * @param target the character to remove the effect from
     */
    @Override
    public void remove(Character target) {
        used = true;
    }

    /**
     * Returns the remaining duration of this effect.
     * A shield lasts indefinitely until used once.
     *
     * @return 1 if unused, 0 if already used
     */
    @Override
    public int getDuration() {
        return used ? 0 : 1;
    }

    /**
     * Gets the type of this status effect.
     *
     * @return the SHIELDED effect type
     */
    @Override
    public StatusEffectType getType() {
        return StatusEffectType.SHIELDED;
    }

    /**
     * Absorbs a portion of the incoming damage if the shield has not yet been used.
     * Once triggered, the shield is marked as used.
     *
     * @param damage the raw incoming damage
     * @return the remaining damage after the shield absorbs up to 15 points
     */
    public int absorb(int damage) {
        if (used) return damage;
        int remaining = Math.max(0, damage - BLOCK_AMOUNT);
        used = true;
        return remaining;
    }
}
