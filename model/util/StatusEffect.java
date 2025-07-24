package model.util;

import model.core.Character;

/**
 * Contract for all turn-based status effects (e.g., <em>Stunned</em>, <em>Poisoned</em>).
 * Implementations are immutable descriptors; BattleController drives the lifecycle.
 *
 * <p>Each concrete implementation must manage its type and duration, and implement
 * all lifecycle hooks to define effect-specific behaviors.</p>
 */
public interface StatusEffect {

    /**
     * Applies the initial effect to the target (e.g., mark as stunned).
     *
     * @param target the character receiving the status (non-null)
     * @throws GameException if the effect cannot be applied
     */
    void applyEffect(Character target) throws GameException;

    /**
     * Called at the start of each of the target’s turns while the effect is active.
     *
     * @param target the affected character (non-null)
     * @throws GameException if an effect-specific rule is violated
     */
    void onTurnStart(Character target) throws GameException;

    /**
     * Called at the end of each of the target’s turns.
     *
     * @param target the affected character (non-null)
     * @throws GameException if an effect-specific rule is violated
     */
    void onTurnEnd(Character target) throws GameException;

    /**
     * Removes the effect and cleans up any temporary state.
     *
     * @param target the character losing the status (non-null)
     * @throws GameException if removal fails
     */
    void remove(Character target) throws GameException;

    /**
     * Gets the remaining turns before this effect expires.
     *
     * @return non-negative remaining turn count
     */
    int getDuration();

    /**
     * Gets the type of this status effect for identity and display logic.
     *
     * @return non-null StatusEffectType identifier
     */
    StatusEffectType getType();
}
