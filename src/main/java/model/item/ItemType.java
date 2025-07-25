package model.item;

/**
 * {@code ItemType} distinguishes how a {@link MagicItem} is activated in
 * <i>Fatal&nbsp;Fantasy:&nbsp;Tactics</i>.
 *
 * <p>Referenced by the inventory and battle systems to determine if a magic
 * item should be consumed upon use or provides a continuous effect.</p>
 *
 * <h3>Specification Traceability</h3>
 * <ul>
 *   <li><strong>MCO2</strong> – Magic Item System: Activation types (Single-Use vs Passive)</li>
 * </ul>
 *
 * @see MagicItem
 * @see Inventory
 */
public enum ItemType {

    /**
     * The item is consumed immediately upon activation.
     * <p>Example: Healing Potion, Energy Flask.</p>
     */
    SINGLE_USE,

    /**
     * The item provides a continuous passive effect while equipped.
     * <p>Example: Amulet of Immunity, Ring of Stamina.</p>
     */
    PASSIVE
}
