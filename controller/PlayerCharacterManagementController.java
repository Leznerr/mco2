package controller;

import model.core.Character;
import model.core.Player;
import view.*;

import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

/** Controller for per-player character management menu. */
public class PlayerCharacterManagementController {
    private final PlayerCharacterManagementView view;
    private final Player player;
    private final GameManagerController gameManagerController;

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
            if (CharacterListViewingView.RETURN.equals(e.getActionCommand())) listView.dispose();
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

    private void openEditCharacter() {
        CharacterEditView editView = new CharacterEditView(view.getPlayerID());
        editView.setActionListener(e -> {
            if (CharacterEditView.RETURN.equals(e.getActionCommand())) editView.dispose();
        });
        // editing logic not fully implemented
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
                    } else {
                        delView.showErrorMessage("Character not found");
                    }
                }
            }
        });
        refreshCharacterList(delView);
    }
}
