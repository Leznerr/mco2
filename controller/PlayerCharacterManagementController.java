package controller;

import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

import model.core.Ability;
import model.core.Character;
import model.core.Player;
import model.item.MagicItem;
import view.InventoryView;
import controller.InventoryController;
import model.service.ClassService;
import model.util.GameException;
import view.CharacterDeleteView;
import view.CharacterEditView;
import view.CharacterListViewingView;
import view.CharacterSpecViewingView;
import view.PlayerCharacterManagementView;

/** Controller for per-player character management menu. */
public class PlayerCharacterManagementController {
    private final PlayerCharacterManagementView view;
    private final Player player;
    private final GameManagerController gameManagerController;
    private final ClassService classService = ClassService.INSTANCE;

    /**
     * Constructor for PayerCharacterManagement Controller
     */
    public PlayerCharacterManagementController(PlayerCharacterManagementView view,
                                               Player player,
                                               GameManagerController gameManagerController) {
        this.view = view;
        this.player = player;
        this.gameManagerController = gameManagerController;
        bind();
    }

    /**
     * Binds button commands in the main player character management view to their corresponding actions.
     * <p>
     * This includes creating, editing, deleting, and viewing characters, managing inventory,
     * and returning to the main menu.
     */
    private void bind() {
        ActionListener l = e -> {
            String cmd = e.getActionCommand();
            switch (cmd) {
                case PlayerCharacterManagementView.VIEW_CHARACTERS -> openCharacterList();
                case PlayerCharacterManagementView.CREATE_CHARACTER -> gameManagerController.handleNavigateToCharacterCreationManagement(player.getName(), view.getPlayerID());
                case PlayerCharacterManagementView.EDIT_CHARACTER -> openEditCharacter();
                case PlayerCharacterManagementView.DELETE_CHARACTER -> openDeleteCharacter();
                case PlayerCharacterManagementView.INVENTORY -> openInventory();
                case PlayerCharacterManagementView.RETURN -> {
                    view.dispose();
                    gameManagerController.navigateBackToMainMenu();
                }
            }
        };
        view.setActionListener(l);
    }

    /**
     * Opens a view that lists all characters for the current player, allowing users
     * to view a character's detailed specifications.
     */
    private void openCharacterList() {
        CharacterListViewingView listView = new CharacterListViewingView(view.getPlayerID());
        listView.setActionListener(e -> {
            String cmd = e.getActionCommand();
            if (CharacterListViewingView.RETURN.equals(cmd)) {
                listView.dispose();
            } else if (CharacterListViewingView.VIEW_CHAR.equals(cmd)) {
                openCharacterSpecView();
            }
        });
        refreshCharacterList(listView);
        listView.setVisible(true);
    }

    /**
     * Refreshes the character list display in the character viewing screen.
     * <p>
     * If no characters exist, a placeholder message is shown.
     * Otherwise, displays each character using their {@code toString()} representation.
     *
     * @param lv the view responsible for displaying the character list
     */
    private void refreshCharacterList(CharacterListViewingView lv) {
        List<Character> chars = player.getCharacters();
        String details = chars.isEmpty() ? "No characters available." :
                chars.stream().map(Character::toString).collect(Collectors.joining("\n\n"));
        lv.updateCharacterList(details);
    }

    /**
     * Refreshes the character list and selection options in the character deletion screen.
     * <p>
     * Displays formatted character details (name, race, class, HP, EP) and updates
     * the selectable character names in the deletion dropdown.
     *
     * @param dv the view responsible for deleting characters
     */
    private void refreshCharacterList(CharacterDeleteView dv) {
        List<Character> chars = player.getCharacters();
        String details;
        if (chars.isEmpty()) {
            details = "No characters available.";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < chars.size(); i++) {
                Character c = chars.get(i);
                sb.append(String.format(
                        "Player %d %s (%s & %s) - HP: %d, EP: %d",
                        dv.getPlayerID(),
                        c.getName(),
                        c.getRaceType(),
                        c.getClassType(),
                        c.getCurrentHp(),
                        c.getCurrentEp()
                ));
                if (i < chars.size() - 1) sb.append("\n\n");
            }
            details = sb.toString();
        }
        dv.updateCharacterList(details);
        dv.setCharacterOptions(chars.stream().map(Character::getName).toArray(String[]::new));
    }

    /**
     * Opens a detailed character specification view based on the selected character.
     * <p>
     * Displays basic character info and lists their abilities.
     * If no characters are available, disables character selection.
     */
    private void openCharacterSpecView() {
        CharacterSpecViewingView specView = new CharacterSpecViewingView(view.getPlayerID());
        specView.resetView();
        java.util.List<Character> chars = player.getCharacters();
        String[] names = chars.stream().map(Character::getName).toArray(String[]::new);
        specView.setCharacterOptions(names);
        if (names.length == 0) {
            specView.updateCharacterDetails("No characters available.");
            specView.setCharacterSelectionEnabled(false);
        } else {
            specView.setCharacterSelectionEnabled(true);
        }
        specView.setActionListener(e -> {
            String cmd = e.getActionCommand();
            if (CharacterSpecViewingView.RETURN.equals(cmd)) {
                specView.dispose();
            } else {
                String name = specView.getSelectedCharacter();
                // ignore events where no character is selected
                if (name == null || name.isBlank()) {
                    // nothing to display
                } else {
                    Character c = player.getCharacter(name).orElse(null);
                    String details = (c == null) ? "Character not found." : c.toString() + "\nAbilities:\n" +
                            c.getAbilities().stream().map(Ability::getName).collect(Collectors.joining("\n"));
                    specView.updateCharacterDetails(details);
                }
            }
        });
        specView.setVisible(true);
    }

    /**
     * Opens the character edit view and populates fields based on the selected character.
     * <p>
     * Allows modification of abilities and equipped magic items.
     */
    private void openEditCharacter() {
        CharacterEditView editView = new CharacterEditView(view.getPlayerID());

        editView.setCharacterOptions(player.getCharacters().stream()
                .map(Character::getName).toArray(String[]::new));

        editView.setActionListener(e -> {
            String cmd = e.getActionCommand();
            if (CharacterEditView.RETURN.equals(cmd)) {
                editView.dispose();
            } else if (CharacterEditView.EDIT.equals(cmd)) {
                handleEditConfirmation(editView);
            } else if (e.getSource() == editView.getCharacterDropdown()) {
                populateEditFields(editView);
            }
        });

        populateEditFields(editView);
        editView.setVisible(true);
    }

    /**
     * Populates all editable fields in the edit view based on the selected character.
     * <p>
     * Sets available ability options based on class and manages visibility of extra ability slots.
     *
     * @param ev the character edit view instance
     */
    private void populateEditFields(CharacterEditView ev) {
        String name = ev.getSelectedCharacter();
        if (name == null) {
            ev.resetFields();
        } else {
            Character c = player.getCharacter(name).orElse(null);
            if (c == null) {
                ev.resetFields();
            } else {
                List<String> abilityNames;
                try {
                    abilityNames = classService.getAvailableAbilities(c.getClassType())
                            .stream().map(Ability::getName).toList();
                } catch (GameException ex) {
                    abilityNames = List.of();
                }
                String[] opts = abilityNames.toArray(new String[0]);
                for (int i = 1; i <= 3; i++) {
                    ev.setAbilityOptions(i, opts);
                }

                boolean allowFour = c.getAbilitySlotCount() > 3;
                ev.setAbility4Visible(allowFour);
                if (allowFour) {
                    ev.setAbilityOptions(4, opts);
                }

                List<Ability> current = c.getAbilities();
                for (int i = 0; i < Math.min(current.size(), 3); i++) {
                    ev.setSelectedAbility(i + 1, current.get(i).getName());
                }
                if (allowFour && current.size() >= 4) {
                    ev.setSelectedAbility(4, current.get(3).getName());
                }

                List<MagicItem> items = c.getInventory().getAllItems();
                String[] itemNames = new String[items.size() + 1];
                itemNames[0] = "None";
                for (int i = 0; i < items.size(); i++) {
                    itemNames[i + 1] = items.get(i).getName();
                }
                ev.setMagicItemOptions(itemNames);
                MagicItem equipped = c.getEquippedItem();
                ev.setSelectedMagicItem(equipped != null ? equipped.getName() : "None");
            }
        }
    }

    /**
     * Validates and applies updates made in the character edit view.
     * <p>
     * Ensures selected abilities are valid and unique, and updates the equipped item.
     * Saves game state and notifies the user of success or error.
     *
     * @param ev the character edit view instance
     */
    private void handleEditConfirmation(CharacterEditView ev) {
        String charName = ev.getSelectedCharacter();
        boolean valid = true;
        if (charName == null) {
            ev.showErrorMessage("No character selected.");
            valid = false;
        }

        if (valid && !ev.confirmCharacterEdit(charName)) {
            valid = false;
        }

        Character c = null;
        if (valid) {
            c = player.getCharacter(charName).orElse(null);
            if (c == null) {
                ev.showErrorMessage("Character not found.");
                valid = false;
            }
        }

        if (valid) {
            try {
                String[] abilityNames = ev.getSelectedAbilities();
                boolean allSelected = true;
                int idx = 0;
                while (idx < abilityNames.length && allSelected) {
                    String a = abilityNames[idx];
                    if (a == null || a.isBlank()) {
                        ev.showErrorMessage("All ability slots must be selected.");
                        allSelected = false;
                    }
                    idx++;
                }
                if (!allSelected) {
                    valid = false;
                }

                if (valid) {
                    java.util.Set<String> unique = new java.util.HashSet<>(java.util.Arrays.asList(abilityNames));
                    if (unique.size() != abilityNames.length) {
                        ev.showErrorMessage("Abilities must be unique.");
                        valid = false;
                    }
                }

                if (valid) {
                    java.util.List<String> validList = classService.getAvailableAbilities(c.getClassType())
                            .stream().map(Ability::getName).toList();
                    boolean selectionValid = true;
                    int i = 0;
                    int limit = Math.min(3, abilityNames.length);
                    while (i < limit && selectionValid) {
                        String a = abilityNames[i];
                        if (!validList.contains(a)) {
                            ev.showErrorMessage("Invalid ability selection for class.");
                            selectionValid = false;
                        }
                        i++;
                    }
                    if (!selectionValid) {
                        valid = false;
                    }
                }

                int expected = Math.min(c.getAbilitySlotCount(), 4);
                if (valid && abilityNames.length != expected) {
                    ev.showErrorMessage("Incorrect number of abilities selected.");
                    valid = false;
                }

                if (valid) {
                    java.util.List<Ability> newAbilities = classService.getAbilitiesByNames(abilityNames);
                    c.setAbilities(newAbilities);

                    String itemName = ev.getSelectedMagicItem();
                    if (itemName == null || itemName.equals("None")) {
                        c.unequipItem();
                    } else {
                        java.util.List<MagicItem> items = c.getInventory().getAllItems();
                        MagicItem chosen = null;
                        int j = 0;
                        while (j < items.size() && chosen == null) {
                            MagicItem mi = items.get(j);
                            if (mi.getName().equalsIgnoreCase(itemName)) {
                                chosen = mi;
                            }
                            j++;
                        }
                        if (chosen != null) {
                            c.equipItem(chosen);
                        }
                    }

                    gameManagerController.handleSaveGameRequest();
                    ev.showInfoMessage("Character updated.");
                    ev.dispose();
                }
            } catch (GameException ex) {
                ev.showErrorMessage(ex.getMessage());
            }
        }
    }

    /**
     * Opens the delete character view, allowing the user to remove a selected character.
     * <p>
     * Handles user confirmation and updates the character list upon successful deletion.
     */
    private void openDeleteCharacter() {
        CharacterDeleteView delView = new CharacterDeleteView(view.getPlayerID());
        delView.setActionListener(e -> {
            String cmd = e.getActionCommand();
            if (CharacterDeleteView.RETURN.equals(cmd)) {
                delView.dispose();
            } else if (CharacterDeleteView.DELETE.equals(cmd)) {
                String name = delView.getSelectedCharacter();
                if (name == null || name.isBlank()) {
                    delView.showErrorMessage("No character selected.");
                } else if (!delView.confirmCharacterDeletion(name)) {
                    // user cancelled deletion
                } else {
                    if (player.removeCharacter(name)) {
                        delView.showInfoMessage("Character " + name + " deleted.");
                        refreshCharacterList(delView);
                        gameManagerController.handleSaveGameRequest();
                    } else {
                        delView.showErrorMessage("Character not found");
                    }
                }
            } else if (e.getSource() == delView.getCharacterDropdown()) {
                String selected = delView.getSelectedCharacter();
                String label = "Player " + delView.getPlayerID();
                if (selected != null && !selected.isBlank()) {
                    label += " - " + selected;
                }
                delView.setCharacterInfoLabel(label);
            }
        });
        refreshCharacterList(delView);
        delView.setCharacterInfoLabel("Player " + delView.getPlayerID());
        delView.setVisible(true);
    }

    /**
     * Opens the inventory management screen for a selected character.
     * <p>
     * If no characters exist, an error message is shown.
     * Otherwise, prompts the user to choose which character's inventory to manage.
     */
    private void openInventory() {
        java.util.List<Character> chars = player.getCharacters();
        if (chars.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(view, "No characters available.",
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        } else {
            String[] names = chars.stream().map(Character::getName).toArray(String[]::new);
            String selected = (String) javax.swing.JOptionPane.showInputDialog(view,
                    "Select Character", "Inventory", javax.swing.JOptionPane.PLAIN_MESSAGE,
                    null, names, names[0]);
            if (selected != null) {
                Character c = player.getCharacter(selected).orElse(null);
                if (c != null) {
                    InventoryView iv = new InventoryView(view.getPlayerID());
                    try {
                        new InventoryController(c, iv, gameManagerController);
                        iv.setVisible(true);
                    } catch (GameException ex) {
                        javax.swing.JOptionPane.showMessageDialog(view, ex.getMessage(),
                                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
}
