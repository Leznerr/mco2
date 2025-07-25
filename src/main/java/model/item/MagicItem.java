package model.item;

import model.util.GameException;
import model.util.InputValidator;

/**
 * <h2>MagicItem</h2>
 * <p>Abstract, immutable data object describing a collectible item.
 * Concrete subclasses (e.g.&nbsp;{@link PassiveItem}, {@link SingleUseItem})
 * add no new state – they merely differentiate behavior handled elsewhere
 * in the domain model.</p>
 *
 * <p><strong>Responsibilities</strong></p>
 * <ul>
 *   <li>Store item metadata (name, description, rarity, type).</li>
 *   <li>Provide read-only getters for UI / controller layers.</li>
 *   <li>Enforce construction-time validation via {@link InputValidator}.</li>
 * </ul>
 *
 * <h3>Specification Traceability</h3>
 * <ul>
 *   <li><strong>MCO2 – Magic Item System:</strong> All magic items derive from this base class.</li>
 * </ul>
 *
 * @see PassiveItem
 * @see SingleUseItem
 * @see Inventory
 */
public abstract class MagicItem {

    /** Human-friendly item title (non-blank). */
    private final String name;

    /** Flavour / effect text shown in UI (non-blank). */
    private final String description;

    /** PASSIVE or SINGLE_USE, never {@code null}. */
    private final ItemType itemType;

    /** Rarity tag (“COMMON”, “RARE”, etc.), cannot be blank. */
    private final String rarity;

    /**
     * Constructs an immutable magic item.
     *
     * @param name        non-blank display name
     * @param description non-blank description
     * @param type        non-null {@link ItemType}
     * @param rarity      non-blank rarity label
     * @throws GameException if any argument violates a pre-condition
     */
    protected MagicItem(String name,
                        String description,
                        ItemType type,
                        String rarity) throws GameException {

        InputValidator.requireNonBlank(name, "item name");
        InputValidator.requireNonBlank(description, "item description");
        InputValidator.requireNonNull(type, "item type");
        InputValidator.requireNonBlank(rarity, "item rarity");

        this.name = name;
        this.description = description;
        this.itemType = type;
        this.rarity = rarity;
    }

    /** @return immutable item name */
    public String getName() {
        return name;
    }

    /** @return immutable item description */
    public String getDescription() {
        return description;
    }

    /** @return the {@link ItemType} (PASSIVE or SINGLE_USE) */
    public ItemType getItemType() {
        return itemType;
    }

    /** @return rarity string (“COMMON”, “RARE”, etc.) */
    public String getRarity() {
        return rarity;
    }

    /**
     * @return a deep copy of this item (concrete type preserved)
     */
    public abstract MagicItem copy();
}
