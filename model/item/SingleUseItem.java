package model.item;

import model.util.GameException;
import model.util.InputValidator;

/**
 * <h2>SingleUseItem</h2>
 *
 * A {@link MagicItem} that is consumed after one activation in
 * <i>Fatal Fantasy: Tactics</i>.
 *
 * <p>This class is <strong>immutable</strong> and holds only metadata.
 * All gameplay logic (e.g., activation effects) is handled externally by
 * the controller or battle system.</p>
 *
 * <h3>Responsibilities & Design</h3>
 * <ul>
 *   <li>Stores descriptive item data (via {@link MagicItem})</li>
 *   <li>Used once, then removed from {@link Inventory}</li>
 *   <li>Supports defensive copying via {@code copy()}</li>
 *   <li>No battle logic included – adheres to SRP and MVC</li>
 * </ul>
 *
 * @see PassiveItem
 * @see Inventory
 */
public final class SingleUseItem extends MagicItem {

    /**
     * Constructs a new single-use magic item.
     *
     * @param name        the item’s display name (non-blank)
     * @param description tooltip text / effect blurb (non-blank)
     * @param rarity      rarity label (e.g., "Common", "Rare")
     * @throws GameException if validation fails
     */
    public SingleUseItem(String name, String description, String rarity)
            throws GameException {

        InputValidator.requireNonBlank(name,        "Item name");
        InputValidator.requireNonBlank(description, "Item description");
        InputValidator.requireNonBlank(rarity,      "Item rarity");

        super(name, description, ItemType.SINGLE_USE, rarity);
    }

    /**
     * Creates a deep copy of this item.
     *
     * @return new {@code SingleUseItem} with identical state
     */
    @Override
    public MagicItem copy() {
        return new SingleUseItem(getName(), getDescription(), getRarity());
    }
}
