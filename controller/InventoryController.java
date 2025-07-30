package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import model.core.Character;
import model.item.MagicItem;
import model.util.GameException;
import model.util.InputValidator;
import view.InventoryView;

import controller.GameManagerController;

/**
 * Controller responsible for handling inventory-related actions
 * such as equipping and unequipping magic items.
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
public final class InventoryController implements ActionListener {

    /** The character whose inventory is being controlled. */
    private final Character character;
    private final GameManagerController gameManagerController;
    private InventoryView view;

    /**
     * Constructs an InventoryController for the specified character.
     *
     * @param character the character whose inventory is to be managed (non-null)
     * @throws GameException if {@code character} is {@code null}
     */
    public InventoryController(Character character,
                               GameManagerController gmController) throws GameException {
        InputValidator.requireNonNull(character, "InventoryController.character");
        InputValidator.requireNonNull(gmController, "gameManagerController");
        this.character = character;
        this.gameManagerController = gmController;
    }

    /** Convenience constructor linking the view immediately. */
    public InventoryController(Character character,
                               InventoryView view,
                               GameManagerController gmController) throws GameException {
        this(character, gmController);
        setView(view);
    }

    /**
     * Refreshes the currently bound inventory view with the character's latest inventory and equipped item.
     * <p>
     * Does nothing if no view is currently bound.
     */
    public void setView(InventoryView view) throws GameException {
        InputValidator.requireNonNull(view, "view");
        this.view = view;
        view.setActionListener(this);
        refreshInventoryDisplay();
    }

    /** Returns an unmodifiable list of items for this character. */
    public List<MagicItem> getInventoryForCharacter() {
        return Collections.unmodifiableList(character.getInventory().getAllItems());
    }

    /** Refreshes the bound view with the latest inventory state. */
    public void refreshInventoryDisplay() {
        if (view != null) {
            view.updateInventory(getInventoryForCharacter(), character.getEquippedItem());
        }
    }

    /**
     * Handles the request to equip a specified magic item to the character.
     * <p>
     * If an item is already equipped, it is replaced by the new one.
     * Triggers game state persistence and view refresh.
     *
     * @param itemToEquip the magic item to equip (non-null)
     * @throws GameException if {@code itemToEquip} is {@code null}
     */
    public void handleEquipItem(MagicItem itemToEquip) {
        try {
            InputValidator.requireNonNull(itemToEquip, "equip item");
            character.getInventory().equipItem(itemToEquip);
            persist();
            refreshInventoryDisplay();
        } catch (GameException e) {
            if (view != null) view.showErrorMessage(e.getMessage());
        }
    }

    /**
     * Handles the request to unequip the currently equipped magic item from the character.
     * <p>
     * If no item is equipped, the operation is ignored.
     * Triggers game state persistence and view refresh.
     */
    public void handleUnequipItem() {
        character.getInventory().unequipItem();
        persist();
        refreshInventoryDisplay();
    }

    private void persist() {
        if (gameManagerController != null) {
            gameManagerController.handleSaveGameRequest();
        }
    }
    
    /**
     * Responds to all GUI events originating from the {@link InventoryView}.
     * <p>
     * This includes equipping, unequipping, viewing, and returning from inventory.
     *
     * @param e the action event received from the view
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (InventoryView.RETURN.equals(cmd)) {
            view.dispose();
        } else if (InventoryView.EQUIP.equals(cmd)) {
            MagicItem sel = view.getSelectedItem();
            if (sel != null) handleEquipItem(sel);
        } else if (InventoryView.UNEQUIP.equals(cmd)) {
            handleUnequipItem();
        } else if (InventoryView.VIEW_ITEM.equals(cmd)) {
            MagicItem sel = view.getSelectedItem();
            if (sel != null) {
                boolean eq = sel.equals(character.getInventory().getEquippedItem());
                view.displayItemDetails(sel, eq);
            } else {
                view.showInfoMessage("Select an item to view.");
            }
        } else if (InventoryView.BACK.equals(cmd)) {
            view.showInventoryList();
        }
    }
}
