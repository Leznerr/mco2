package model.item;

import model.util.GameException;
import model.util.InputValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public final class Inventory {

    /** A unified list of all magic items the character possesses. */
    private final List<MagicItem> items;

    /** A reference to the currently equipped magic item, which must also be in the items list. */
    private MagicItem equippedItem;

    /**
     * Constructs an empty inventory.
     */
    public Inventory() {
        this.items = new ArrayList<>();
        this.equippedItem = null;
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
     * Returns an unmodifiable view of all items in the inventory.
     *
     * @return a read-only list of all magic items.
     */
    public List<MagicItem> getAllItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Adds a new magic item to the inventory.
     *
     * @param item The non-null item to add.
     */
    public void addItem(MagicItem item) {
        InputValidator.requireNonNull(item, "Item to add");
        items.add(item);
    }

    /**
     * Removes a magic item from the inventory. If the item is equipped, it will also be unequipped.
     *
     * @param item The non-null item to remove.
     * @return {@code true} if the item was found and removed, {@code false} otherwise.
     */
    public boolean removeItem(MagicItem item) {
        InputValidator.requireNonNull(item, "Item to remove");
        if (item.equals(equippedItem)) {
            unequipItem();
        }
        return items.remove(item);
    }

    /**
     * Equips an item from the inventory. Any previously equipped item will be unequipped first.
     *
     * @param item The magic item to equip. It must be present in the inventory.
     * @throws GameException if the item is null or not found in the inventory.
     */
    public void equipItem(MagicItem item) throws GameException {
        InputValidator.requireNonNull(item, "Item to equip");
        if (!items.contains(item)) {
            throw new GameException("Cannot equip item '" + item.getName() + "' because it is not in the inventory.");
        }
        this.equippedItem = item;
    }

    /**
     * Unequips the currently equipped item. The item remains in the inventory.
     */
    public void unequipItem() {
        this.equippedItem = null;
    }

    public void useSingleUseItem(SingleUseItem item) throws GameException {
    InputValidator.requireNonNull(item, "Single-use item to use");
    if (!items.contains(item)) {
        throw new GameException("Cannot use item: not found in inventory.");
    }
    // Remove from inventory (consume it)
    items.remove(item);
    if (item.equals(equippedItem)) {
        equippedItem = null;
    }
    // NO call to item.activate(), as per your spec
    // The controller or battle system should process the effect externally
}

}