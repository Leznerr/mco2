package model.item;

import model.util.GameException;

/**
 * <h2>PassiveItem</h2>
 * A {@link MagicItem} that provides a continuous passive effect while equipped
 * by a {@code Character} in <i>Fatal Fantasy: Tactics</i>.
 *
 * <p>This class is <strong>immutable</strong> and carries no additional fields
 * beyond its {@link MagicItem} superclass. Its role is defined by the
 * {@link ItemType#PASSIVE} tag.</p>
 *
 * <h3>Specification Mapping</h3>
 * <ul>
 *   <li>MCO2 – Magic Item System → “Item can be equipped for passive effect.”</li>
 * </ul>
 *
 * @see MagicItem
 * @see SingleUseItem
 * @see Inventory
 */
public final class PassiveItem extends MagicItem {

    /**
     * Constructs a new passive magic item.
     *
     * @param name        the item's display name (non-blank)
     * @param description tooltip text / effect blurb (non-blank)
     * @param rarity      rarity label (e.g., "Common", "Legendary")
     * @throws GameException if any parameter fails validation
     */
    public PassiveItem(String name, String description, String rarity) throws GameException {
        super(name, description, ItemType.PASSIVE, rarity);
    }

    /**
     * Creates a deep copy of this passive item.
     *
     * @return new {@code PassiveItem} with identical state
     */
    @Override
    public MagicItem copy() {
        return new PassiveItem(getName(), getDescription(), getRarity());
    }
}
