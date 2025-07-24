package controller;

import model.core.Character;
import model.item.MagicItem;
import model.item.SingleUseItem;
import model.util.GameException;
import model.util.InputValidator;

/**
 * Controller responsible for handling inventory-related actions
 * such as equipping magic items, unequipping, and using single-use items.
 * <p>
 * Acts as the intermediary between the GUI and the model layer's
 * inventory functionality, enforcing validation and encapsulation.
 *
 * <h3>Design Notes:</h3>
 * <ul>
 *     <li><strong>MVC Compliance:</strong> Controller mediates all inventory logic.</li>
 *     <li><strong>Encapsulation:</strong> Character’s inventory is manipulated solely via this controller.</li>
 *     <li><strong>Input Validation:</strong> All public methods validate arguments and state before acting.</li>
 * </ul>
 */
public final class InventoryController {

    /** The character whose inventory is being controlled. */
    private final Character character;

    /**
     * Constructs an InventoryController for the specified character.
     *
     * @param character the character whose inventory is to be managed (non-null)
     * @throws GameException if {@code character} is {@code null}
     */
    public InventoryController(Character character) throws GameException {
        InputValidator.requireNonNull(character, "InventoryController.character");
        this.character = character;
    }

    /**
     * Handles a request to equip a passive {@link MagicItem}.
     * If a previous item is equipped, it will be replaced.
     *
     * @param itemToEquip the magic item to equip (non-null)
     * @throws GameException if {@code itemToEquip} is {@code null}
     */
    public void handleEquipItemRequest(MagicItem itemToEquip) throws GameException {
        InputValidator.requireNonNull(itemToEquip, "handleEquipItemRequest.itemToEquip");
        character.getInventory().equipItem(itemToEquip);
    }

    /**
     * Handles a request to unequip the currently equipped passive magic item.
     * If no item is equipped, no action occurs.
     */
    public void handleUnequipItemRequest() {
        character.getInventory().unequipItem();
    }

    /**
     * Handles a request to use a {@link SingleUseItem}.
     * The item is consumed immediately upon use.
     *
     * @param itemToUse the single-use item to activate (non-null)
     * @throws GameException if {@code itemToUse} is {@code null}
     */
    public void handleUseItemRequest(SingleUseItem itemToUse) throws GameException {
        InputValidator.requireNonNull(itemToUse, "handleUseItemRequest.itemToUse");
        character.getInventory().useSingleUseItem(itemToUse);
    }
}
