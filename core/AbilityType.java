package model.core;


/**
 * Enumeration of ability categories used in <em>Fatal Fantasy: Tactics</em>.
 *
 * <p>Each {@code AbilityType} defines the functional role of a character's
 * ability, influencing both game mechanics and UI rendering.</p>
 *
 * <ul>
 *   <li>{@link #DAMAGE} ‒ Direct offensive abilities causing HP reduction.</li>
 *   <li>{@link #HEAL} ‒ Restorative actions replenishing HP.</li>
 *   <li>{@link #ENERGY_GAIN} ‒ Moves that restore or grant EP.</li>
 *   <li>{@link #DEFENSE} ‒ Buff abilities that increase defense temporarily.</li>
 *   <li>{@link #EVADE} ‒ Evasion-enhancing techniques to avoid attacks.</li>
 *   <li>{@link #UTILITY} ‒ Miscellaneous or tactical effects not covered above.</li>
 * </ul>
 */
public enum AbilityType {

    /** Direct HP-damaging offensive abilities. */
    DAMAGE,

    /** HP restoration abilities. */
    HEAL,

    /** EP restoration or generation moves. */
    ENERGY_GAIN,

    /** Defensive buffs to mitigate incoming damage. */
    DEFENSE,

    /** Increases chance to avoid attacks. */
    EVADE,

    /** Tactical effects like buffs, debuffs, or other special actions. */
    UTILITY,

    /** Applies persistent status effects to alter target behavior. */
    STATUS;
}
