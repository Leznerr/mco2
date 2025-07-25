package model.battle;

/**
 * Marker interface for universal battle actions that are not tied to a
 * specific ability or item, such as defending or recharging.
 *
 * <p>Extends {@link Move} so these actions participate in the same
 * execution flow as abilities and items.</p>
 */
public interface MoveAction extends Move {
    // no extra methods; serves only for semantic grouping
}
