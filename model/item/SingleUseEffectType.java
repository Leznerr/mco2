package model.item;

/**
 * Enumerates supported effects for {@link SingleUseItem}s.
 */
public enum SingleUseEffectType {
    /** Restores HP to the user. */
    HEAL_HP,
    /** Restores EP to the user. */
    RESTORE_EP,
    /** Revives the user from KO with a percentage of max HP. */
    REVIVE,
    /** Grants temporary immunity from all damage. */
    GRANT_IMMUNITY
}
