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

    /**
     * Constructor for Evade Effect
     */
    public EvadeEffect() {
        this.remainingTurns = DURATION_TURNS;
    }

    /**
     * Applies the evasion effect to the target character.
     * No immediate changes to stats are applied; effect is tracked in the status list.
     *
     * @param target the character receiving the effect
     * @throws GameException if the target is null
     */
    @Override
    public void applyEffect(Character target) throws GameException {
        InputValidator.requireNonNull(target, "EvadeEffect target");
        // No immediate stat change
    }

    /**
     * Called at the start of the target character's turn.
     * Decrements the duration of the evasion effect.
     *
     * @param target the character affected by the effect
     * @throws GameException if the target is null
     */
    @Override
    public void onTurnStart(Character target) throws GameException {
        InputValidator.requireNonNull(target, "EvadeEffect target");
        decrementDuration();
    }

    /**
     * Called at the end of the target character's turn.
     * Currently does nothing for the evasion effect.
     *
     * @param target the character affected by the effect
     */
    @Override
    public void onTurnEnd(Character target) {
        // No end of turn behaviour
    }

    /**
     * Removes the evasion effect from the target character.
     * Currently has no cleanup logic beyond validation.
     *
     * @param target the character losing the effect
     * @throws GameException if the target is null
     */
    @Override
    public void remove(Character target) throws GameException {
        InputValidator.requireNonNull(target, "EvadeEffect target");
    }

    /**
     * Returns the remaining duration (in turns) of the evasion effect.
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
     * @return the status effect type (EVADING)
     */
    @Override
    public StatusEffectType getType() {
        return StatusEffectType.EVADING;
    }

    /**
     * Reduces the duration counter by 1 turn, if greater than 0.
     */
    private void decrementDuration() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    /**
     * Returns a string representation of the effect, including remaining turns.
     *
     * @return human-readable effect description
     */
    @Override
    public String toString() {
        return "Evading (" + remainingTurns + " turns left)";
    }
}
