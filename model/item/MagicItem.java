package model.item;

import model.util.GameException;
import model.util.InputValidator;
import java.io.Serializable;

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
public abstract class MagicItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Human-friendly item title (non-blank). */
    private final String name;

    /** Flavour / effect text shown in UI (non-blank). */
    private final String description;

    /** PASSIVE or SINGLE_USE, never {@code null}. */
    private final ItemType itemType;

    /** Rarity tier of this item. */
    private final RarityType rarityType;

    /** Cached drop chance percentage from {@link #rarityType}. */
    private final int dropChance;

    /**
     * Constructs an immutable magic item.
     *
     * @param name        non-blank display name
     * @param description non-blank description
     * @param type        non-null {@link ItemType}
     * @param rarity      rarity tier of the item
     * @throws GameException if any argument violates a pre-condition
     */
    protected MagicItem(String name,
                        String description,
                        ItemType type,
                        RarityType rarity) throws GameException {

        InputValidator.requireNonBlank(name, "item name");
        InputValidator.requireNonBlank(description, "item description");
        InputValidator.requireNonNull(type, "item type");
        InputValidator.requireNonNull(rarity, "item rarity");

        this.name = name;
        this.description = description;
        this.itemType = type;
        this.rarityType = rarity;
        this.dropChance = rarity.getDropChance();
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

    /**
     * Determines equality based on immutable item properties and concrete type.
     * Two items are considered equal if they are of the same class and all
     * core fields match.
     *
     * @param o the object to compare with
     * @return {@code true} if the objects are logically equal
     */
    @Override
    public boolean equals(Object o) {
        boolean result;
        if (this == o) {
            result = true;
        } else if (o == null || getClass() != o.getClass()) {
            result = false;
        } else {
            MagicItem that = (MagicItem) o;
            result = name.equals(that.name)
                    && description.equals(that.description)
                    && itemType == that.itemType
                    && rarityType == that.rarityType;
        }
        return result;
    }

    /**
     * Computes a hash code consistent with {@link #equals(Object)}.
     *
     * @return hash code based on immutable fields
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, description, itemType, rarityType);
    }

    /**
     * Returns a short display string including name and type.
     */
    @Override
    public String toString() {
        return getName() + " (" + itemType + ")";
    }

    /** @return rarity string ("Common", "Rare", etc.) */
    public String getRarity() {
        return rarityType.toString();
    }

    /** @return rarity enumeration value */
    public RarityType getRarityType() {
        return rarityType;
    }

    /** @return percentage chance this item’s rarity drops */
    public int getDropChance() {
        return dropChance;
    }

    /**
     * @return a deep copy of this item (concrete type preserved)
     */
    public abstract MagicItem copy();
}
