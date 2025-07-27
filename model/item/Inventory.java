package model.item;

import model.util.GameException;
import model.util.InputValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;

/**
 * Represents a character's inventory of magic items in <i>Fatal Fantasy: Tactics</i>.
 *
 * <h2>Inventory Management</h2>
 * <ul>
 * <li><strong>Unified Storage:</strong> A single list holds all magic items, regardless of type.</li>
 * <li><strong>Equipped Item:</strong> At most one item from the inventory can be equipped at a time.</li>
 * <li><strong>Encapsulation:</strong> Exposes read-only views of its contents.</li>
 * </ul>
 *
 * <h3>Specification Mapping</h3>
 * <ul>
 * <li><strong>MCO2 Core:</strong> Magic Item System (inventory, equipment, consumption).</li>
 * </ul>
 */
public final class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;

    /** A unified list of all magic items the character possesses. Always initialized. Never null. */
    private List<MagicItem> items = new ArrayList<>();

    /** A reference to the currently equipped magic item, which must also be in the items list. */
    private MagicItem equippedItem;

    /**
     * Constructs an empty inventory with no items and nothing equipped.
     */
    public Inventory() {
        this.equippedItem = null;
    }

    /**
     * Creates a new inventory that is a deep copy of the supplied one. Each
     * contained {@link MagicItem} is cloned via {@link MagicItem#copy()} so the
     * resulting inventory is completely independent of the original.  The
     * equipped item state is preserved if applicable.
     *
     * @param other the inventory to copy (must not be {@code null})
     */
    public Inventory(Inventory other) {
        this();
        InputValidator.requireNonNull(other, "other inventory");
        for (MagicItem item : other.items) {
            MagicItem copy = item.copy();
            items.add(copy);
            if (item.equals(other.equippedItem)) {
                equippedItem = copy;
            }
        }
    }

    /**
     * Returns the currently equipped magic item, if any.
     *
     * @return equipped {@link MagicItem}, or {@code null} if none.
     */
    public MagicItem getEquippedItem() {
        return equippedItem;
    }

    /**
     * Returns an unmodifiable snapshot of all items currently stored in this
     * inventory.  The returned list is a defensive copy and cannot be modified
     * by the caller.
     *
     * @return an unmodifiable list containing all magic items
     */
    public List<MagicItem> getAllItems() {
        return Collections.unmodifiableList(new ArrayList<>(items));
    }

    /**
     * Checks whether the specified item is currently stored in this inventory.
     *
     * @param item the item to look for (must not be {@code null})
     * @return {@code true} if the inventory contains the item
     */
    public boolean hasItem(MagicItem item) {
        InputValidator.requireNonNull(item, "Item to check");
        return items.contains(item);
    }


     *
     * @param item the item to add (must not be {@code null})
     */
    public void addItem(MagicItem item) {
        InputValidator.requireNonNull(item, "Item to add");
        if (items.contains(item)) {
            return; // prevent duplicates
        }
        items.add(item);
    }

    /**
     * Removes a magic item from the inventory. If the removed item was
     * currently equipped it will be automatically unequipped.  Attempting to
     * remove an item that is not present has no effect.
     *
     * @param item the item to remove (must not be {@code null})
     * @return {@code true} if the item existed and was removed
     */
    public boolean removeItem(MagicItem item) {
        InputValidator.requireNonNull(item, "Item to remove");
        if (item.equals(equippedItem)) {
            unequipItem();
        }
        return items.remove(item);
    }

    /**
     * Sets the provided item as this inventory's equipped item. Only one item
     * may be equipped at any time; any previously equipped item will simply be
     * replaced. The item must already exist within this inventory.
     *
     * @param item the item to equip (must not be {@code null})
     * @throws GameException if the item is not currently held in the inventory
     */
    public void equipItem(MagicItem item) throws GameException {
        InputValidator.requireNonNull(item, "Item to equip");
        if (!items.contains(item)) {
            throw new GameException("Cannot equip item '" + item.getName() + "' because it is not in the inventory.");
        }
        this.equippedItem = item;
    }

    /**
     * Clears the equipped item slot. The unequipped item remains in the
     * inventory list.
     */
    public void unequipItem() {
        this.equippedItem = null;
    }

    /**
     * Consumes a {@link SingleUseItem} from the inventory. The item is removed
     * and the equipped slot cleared if it was equipped. This method performs no
     * activation logic; effect handling is expected elsewhere.
     *
     * @param item the single-use item to consume (must not be {@code null})
     * @throws GameException if the item is not present in the inventory
     */
    public void useSingleUseItem(SingleUseItem item) throws GameException {
        InputValidator.requireNonNull(item, "Single-use item to use");
        if (!items.contains(item)) {
            throw new GameException("Cannot use item: not found in inventory.");
        }

        items.remove(item);
        if (item.equals(equippedItem)) {
            equippedItem = null;
        }
        // Item effect application occurs in controller/battle logic
    }

    /**
     * Ensures the items list is not null when deserialised.
     */
    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (items == null) {
            items = new ArrayList<>();
        }
    }

} 