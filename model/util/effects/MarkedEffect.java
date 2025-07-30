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

    /**
     * Applies the marked effect to the target character.
     * Does not alter stats directly, but sets up for increased damage.
     *
     * @param target the character receiving the effect
     * @throws GameException if the target is null
     */
    @Override
    public void applyEffect(Character target) throws GameException {
        InputValidator.requireNonNull(target, "MarkedEffect target");
    }

    /**
     * Called at the start of the target's turn.
     * Decreases the effect's remaining duration by one turn.
     *
     * @param target the character affected by the effect
     * @throws GameException if the target is null
     */
    @Override
    public void onTurnStart(Character target) throws GameException {
        InputValidator.requireNonNull(target, "MarkedEffect target");
        if (remaining > 0) remaining--;
    }

    /**
     * Called at the end of the target's turn.
     * This effect does nothing at turn end.
     *
     * @param target the character affected by the effect
     */
    @Override
    public void onTurnEnd(Character target) {
        // no-op
    }

    /**
     * Removes the marked effect from the target.
     * Currently no additional cleanup is performed.
     *
     * @param target the character losing the effect
     */
    @Override
    public void remove(Character target) {}

    /**
     * Returns the remaining duration (in turns) of the marked effect.
     *
     * @return number of turns left
     */
    @Override
    public int getDuration() {
        return remaining;
    }

    /**
     * Returns the type of this status effect.
     *
     * @return the status effect type (MARKED)
     */
    @Override
    public StatusEffectType getType() {
        return StatusEffectType.MARKED;
    }
}
