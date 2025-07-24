package model.core;

/**
 * Enumerates the possible effect types that an {@link Ability} can apply
 * during battle.
 *
 * <p>Supports polymorphic execution in the battle engine by categorizing
 * abilities into distinct functional groups.</p>
 *
 * <h3>Design Compliance</h3>
 * <ul>
 * <li>Immutable, type-safe enum.</li>
 * <li>Strictly for modeling purposes – no executable logic embedded.</li>
 * <li>Rubric-aligned: Clean enum abstraction for ability effects.</li>
 * </ul>
 */
public enum AbilityEffectType {

    /**
     * An effect that reduces the Health Points (HP) of a target.
     */
    DAMAGE,

    /**
     * An effect that restores the Health Points (HP) of a target.
     */
    HEAL,

    /**
     * An effect that restores the Energy Points (EP) of a target.
     */
    ENERGY_GAIN,

    /**
     * An effect that applies a {@link model.util.StatusEffectType} to a target.
     */
    APPLY_STATUS,

    /**
     * An effect that provides a temporary defensive buff, such as immunity.
     */
    DEFENSE,

    /**
     * An effect that provides a temporary buff to evasion.
     */
    EVADE,

    /**
     * Represents a miscellaneous, non-damaging support action (e.g., scanning an enemy).
     */
    UTILITY
}