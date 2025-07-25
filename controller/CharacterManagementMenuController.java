package controller;

import model.core.Player;
import view.CharacterManagementMenuView;

import java.awt.event.ActionListener;
import java.util.List;

/**
 * Controller for the first character management screen where a player is chosen.
 */
public class CharacterManagementMenuController {
    private final CharacterManagementMenuView view;
    private final List<Player> players;
    private final SceneManager sceneManager;

    public CharacterManagementMenuController(CharacterManagementMenuView view, List<Player> players, SceneManager sceneManager) {
        this.view = view;
        this.players = players;
        this.sceneManager = sceneManager;
        bind();
    }

    private void bind() {
        ActionListener l = e -> {
            String cmd = e.getActionCommand();
            switch (cmd) {
                case CharacterManagementMenuView.MANAGE_PLAYER1 -> openPlayerView(0);
                case CharacterManagementMenuView.MANAGE_PLAYER2 -> openPlayerView(1);
                case CharacterManagementMenuView.RETURN_TO_MENU -> sceneManager.showMainMenu();
            }
        };
        view.setActionListener(l);
    }

    private void openPlayerView(int idx) {
        if (idx >= 0 && idx < players.size()) {
            Player p = players.get(idx);
            sceneManager.showPlayerCharacterManagement(p);
        }
    }
}
