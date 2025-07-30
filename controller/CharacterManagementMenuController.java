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

    /**
     * Constructor for CharacterManagementMenuController
     */
    public CharacterManagementMenuController(CharacterManagementMenuView view, List<Player> players, SceneManager sceneManager) {
        this.view = view;
        this.players = players;
        this.sceneManager = sceneManager;
        bind();
        updateLabels();
    }

    /**
     * Binds the view's buttons to actions handled by this controller.
     * <p>
     * Responds to player selection and return-to-menu actions using command strings
     * defined in {@link CharacterManagementMenuView}.
     * </p>
     */
    private void bind() {
        ActionListener l = e -> {
            String cmd = e.getActionCommand();
            switch (cmd) {
                case CharacterManagementMenuView.MANAGE_PLAYER1 -> openPlayerView(0);
                case CharacterManagementMenuView.MANAGE_PLAYER2 -> openPlayerView(1);
                case CharacterManagementMenuView.RETURN_TO_MENU -> {
                    // Close this menu before returning to the main menu so the
                    // application can exit cleanly if no other windows remain.
                    view.dispose();
                    sceneManager.showMainMenu();
                }
            }
        };
        view.setActionListener(l);
    }

    /**
     * Opens the character management view for the selected player index.
     *
     * @param idx index of the player to manage (0 for Player 1, 1 for Player 2)
     */
    private void openPlayerView(int idx) {
        if (idx >= 0 && idx < players.size()) {
            Player p = players.get(idx);
            sceneManager.showPlayerCharacterManagement(p);
        }
    }

    /**
     * Refreshes the view's player labels to reflect the current player list.
     */
    public void refresh() {
        updateLabels();
    }

    /**
     * Updates the player name labels in the view based on the player list.
     * <p>
     * Shows an info dialog if no players are registered.
     * </p>
     */
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
