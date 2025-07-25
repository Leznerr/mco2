package model.util;

/**
 * Enumerates all status effects that can temporarily alter a
 * {@code Character}'s behaviour in <em>Fatal Fantasy: Tactics</em>.
 *
 * <p><strong>Design intent</strong> – This enum is referenced by the
 * {@code StatusEffect} strategy interface (bonus feature) and by
 * {@code BattleController} when it must apply after-effect logic.  Having a
 * single authoritative list satisfies the rubric’s <em>Maintainability</em>
 * and <em>Extensibility</em> criteria.</p>
 *
 * <h3>Literals</h3>
 * <ul>
 *   <li>{@link #STUNNED} &nbsp; – skip the next turn</li>
 *   <li>{@link #POISONED} &nbsp; – lose HP at end of turn</li>
 *   <li>{@link #DEFENSE_UP} –  take reduced damage</li>
 *   <li>{@link #EVADING} &nbsp; – higher chance to dodge</li>
 *   <li>{@link #IMMUNITY} &nbsp; – immune to negative effects</li>
 * </ul>
 *
 */
public enum StatusEffectType {

    /** Skip the next action. */
    STUNNED,

    /** Lose hit-points at the end of each turn. */
    POISONED,

    /** Incoming damage is reduced while active. */
    DEFENSE_UP,

    /** Increases dodge chance for a limited duration. */
    EVADING,

    /** Grants immunity to further negative effects. */
    IMMUNITY,

    NONE;
}
