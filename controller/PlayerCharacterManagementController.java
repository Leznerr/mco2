package controller;

import model.core.Ability;
import model.core.Character;
import model.core.ClassType;
import model.core.Player;
import model.item.MagicItem;
import model.service.ClassService;
import model.util.GameException;
import view.*;

import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

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
                case PlayerCharacterManagementView.RETURN -> view.dispose();
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
    }

    private void refreshCharacterList(CharacterListViewingView lv) {
        List<Character> chars = player.getCharacters();
        String details = chars.isEmpty() ? "No characters available." :
                chars.stream().map(Character::toString).collect(Collectors.joining("\n\n"));
        lv.updateCharacterList(details);
    }

    private void refreshCharacterList(CharacterDeleteView dv) {
        List<Character> chars = player.getCharacters();
        String details = chars.isEmpty() ? "No characters available." :
                chars.stream().map(Character::toString).collect(Collectors.joining("\n\n"));
        dv.updateCharacterList(details);
        dv.setCharacterOptions(chars.stream().map(Character::getName).toArray(String[]::new));
    }

    private void openCharacterSpecView() {
        CharacterSpecViewingView specView = new CharacterSpecViewingView(view.getPlayerID());
        specView.setCharacterOptions(player.getCharacters().stream()
                .map(Character::getName).toArray(String[]::new));
        specView.setActionListener(e -> {
            String cmd = e.getActionCommand();
            if (CharacterSpecViewingView.RETURN.equals(cmd)) {
                specView.dispose();
            } else {
                String name = specView.getSelectedCharacter();
                Character c = player.getCharacter(name).orElse(null);
                String details = (c == null) ? "Character not found." : c.toString() + "\nAbilities:\n" +
                        c.getAbilities().stream().map(Ability::getName).collect(Collectors.joining("\n"));
                specView.updateCharacterDetails(details);
            }
        });
        specView.resetView();
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
        ev.setAbilityOptions(1, opts);
        ev.setAbilityOptions(2, opts);
        ev.setAbilityOptions(3, opts);

        List<Ability> current = c.getAbilities();
        if (current.size() >= 3) {
            ev.setSelectedAbility(1, current.get(0).getName());
            ev.setSelectedAbility(2, current.get(1).getName());
            ev.setSelectedAbility(3, current.get(2).getName());
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
            List<Ability> newAbilities = classService.getAbilitiesByNames(ev.getSelectedAbilities());
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
                if (name != null && delView.confirmCharacterDeletion(name)) {
                    if (player.removeCharacter(name)) {
                        delView.showInfoMessage("Deleted " + name);
                        refreshCharacterList(delView);
                        gameManagerController.handleSaveGameRequest();
                    } else {
                        delView.showErrorMessage("Character not found");
                    }
                }
            }
        });
        refreshCharacterList(delView);
    }
}
