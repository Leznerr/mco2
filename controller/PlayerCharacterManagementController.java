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

    public PlayerCharacterManagementController(PlayerCharacterManagementView view,
                                               Player player,
                                               GameManagerController gameManagerController) {
        this.view = view;
        this.player = player;
        this.gameManagerController = gameManagerController;
        bind();
    }

    private void bind() {
        ActionListener l = e -> {
            String cmd = e.getActionCommand();
            switch (cmd) {
                case PlayerCharacterManagementView.VIEW_CHARACTERS -> openCharacterList();
                case PlayerCharacterManagementView.CREATE_CHARACTER -> gameManagerController.handleNavigateToCharacterCreationManagement(player.getName());
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

    private void refreshCharacterList(CharacterListViewingView lv) {
        List<Character> chars = player.getCharacters();
        String details = chars.isEmpty() ? "No characters available." :
                chars.stream().map(Character::toString).collect(Collectors.joining("\n\n"));
        lv.updateCharacterList(details);
    }

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
                        "Char %d %s (%s & %s) - HP: %d, EP: %d",
                        i + 1,
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
                    return;
                }
                Character c = player.getCharacter(name).orElse(null);
                String details;
                if (c == null) {
                    details = "Character not found.";
                } else {
                    java.util.List<String> abilityNames = new java.util.ArrayList<>();
                    for (int i = 0; i < c.getAbilitySlots(); i++) {
                        if (i < c.getAbilities().size()) {
                            abilityNames.add(c.getAbilities().get(i).getName());
                        } else {
                            abilityNames.add("Empty Slot");
                        }
                    }
                    details = c.toString() + "\nAbilities:\n" + String.join("\n", abilityNames);
                }
                specView.updateCharacterDetails(details);
            }
        });
        specView.setVisible(true);
    }

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

    private void populateEditFields(CharacterEditView ev) {
        String name = ev.getSelectedCharacter();
        if (name == null) {
            ev.resetFields();
            return;
        }

        Character c = player.getCharacter(name).orElse(null);
        if (c == null) return;

        List<String> abilityNames;
        try {
            abilityNames = classService.getAvailableAbilities(c.getClassType())
                    .stream().map(Ability::getName).toList();
        } catch (GameException ex) {
            abilityNames = List.of();
        }
        String[] opts = abilityNames.toArray(new String[0]);
        int slots = c.getAbilitySlots();
        ev.setAbilityCount(slots);
        for (int i = 1; i <= slots; i++) {
            ev.setAbilityOptions(i, opts);
        }

        List<Ability> current = c.getAbilities();
        for (int i = 0; i < Math.min(current.size(), slots); i++) {
            ev.setSelectedAbility(i + 1, current.get(i).getName());
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

    private void handleEditConfirmation(CharacterEditView ev) {
        String charName = ev.getSelectedCharacter();
        if (charName == null) {
            ev.showErrorMessage("No character selected.");
            return;
        }

        if (!ev.confirmCharacterEdit(charName)) {
            return;
        }

        Character c = player.getCharacter(charName).orElse(null);
        if (c == null) {
            ev.showErrorMessage("Character not found.");
            return;
        }

        try {
            String[] abilityNames = ev.getSelectedAbilities();
            // Ensure all abilities are chosen
            for (String a : abilityNames) {
                if (a == null || a.isBlank()) {
                    ev.showErrorMessage("All ability slots must be selected.");
                    return;
                }
            }

            // Check for duplicates
            java.util.Set<String> unique = new java.util.HashSet<>(java.util.Arrays.asList(abilityNames));
            if (unique.size() != abilityNames.length) {
                ev.showErrorMessage("Abilities must be unique.");
                return;
            }

            // Validate abilities for the character's class (first three only)
            java.util.List<String> valid = classService.getAvailableAbilities(c.getClassType())
                    .stream().map(Ability::getName).toList();
            for (int i = 0; i < Math.min(3, abilityNames.length); i++) {
                String a = abilityNames[i];
                if (!valid.contains(a)) {
                    ev.showErrorMessage("Invalid ability selection for class.");
                    return;
                }
            }

            int expected = ev.getAbilityCount();
            if (abilityNames.length != expected) {
                ev.showErrorMessage("Incorrect number of abilities selected.");
                return;
            }

            java.util.List<Ability> newAbilities = classService.getAbilitiesByNames(abilityNames);
            c.setAbilities(newAbilities);

            String itemName = ev.getSelectedMagicItem();
            if (itemName == null || itemName.equals("None")) {
                c.unequipItem();
            } else {
                for (MagicItem mi : c.getInventory().getAllItems()) {
                    if (mi.getName().equalsIgnoreCase(itemName)) {
                        c.equipItem(mi);
                        break;
                    }
                }
            }

            gameManagerController.handleSaveGameRequest();
            ev.showInfoMessage("Character updated.");
            ev.dispose();
        } catch (GameException ex) {
            ev.showErrorMessage(ex.getMessage());
        }
    }

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
                    return;
                }

                if (!delView.confirmCharacterDeletion(name)) {
                    return;
                }

                if (player.removeCharacter(name)) {
                    delView.showInfoMessage("Character " + name + " deleted.");
                    refreshCharacterList(delView);
                    gameManagerController.handleSaveGameRequest();
                } else {
                    delView.showErrorMessage("Character not found");
                }
            }
        });
        refreshCharacterList(delView);
        delView.setVisible(true);
    }

    /** Opens the inventory management view for a selected character. */
    private void openInventory() {
        java.util.List<Character> chars = player.getCharacters();
        if (chars.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(view, "No characters available.",
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        String[] names = chars.stream().map(Character::getName).toArray(String[]::new);
        String selected = (String) javax.swing.JOptionPane.showInputDialog(view,
                "Select Character", "Inventory", javax.swing.JOptionPane.PLAIN_MESSAGE,
                null, names, names[0]);
        if (selected == null) return;
        Character c = player.getCharacter(selected).orElse(null);
        if (c == null) return;
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
