package controller;

import model.core.Player;
import view.CharacterManagementMenuView;
import javax.swing.JOptionPane;

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
        updateLabels();
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

    /** Refresh the player button labels based on the current player list. */
    public void refresh() {
        updateLabels();
    }

    private void updateLabels() {
        if (players.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "No players registered.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            view.setPlayer1Name(null);
            view.setPlayer2Name(null);
        } else {
            if (players.size() > 0) {
                view.setPlayer1Name(players.get(0).getName());
            }
            if (players.size() > 1) {
                view.setPlayer2Name(players.get(1).getName());
            } else {
                view.setPlayer2Name(null);
            }
        }
    }
}
