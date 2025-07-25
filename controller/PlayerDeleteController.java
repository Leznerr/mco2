package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;

import model.core.Player;
import persistence.SaveLoadService;
import view.PlayerDeleteView;
import model.util.GameException;

/**
 * Controller handling player deletion workflow.
 */
public class PlayerDeleteController implements ActionListener {

    private final PlayerDeleteView view;
    private final GameManagerController gameManager;

    /**
     * Constructs a controller for the given view and game manager.
     */
    public PlayerDeleteController(PlayerDeleteView view, GameManagerController gameManager) {
        this.view = view;
        this.gameManager = gameManager;
        this.view.setActionListener(this);
        refresh();
    }

    /**
     * Refreshes dropdowns and list text with current players.
     */
    public void refresh() {
        List<Player> players = gameManager.getPlayers();
        String[] names = players.stream().map(Player::getName).toArray(String[]::new);
        view.setPlayerOptions(names);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < names.length; i++) {
            sb.append(i + 1).append(". ").append(names[i]).append("\n");
        }
        view.updatePlayerList(sb.toString());
        view.resetDropdowns();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (PlayerDeleteView.RETURN.equals(cmd)) {
            view.dispose();
            // Back to player registration/main menu handled by SceneManager
        } else if (PlayerDeleteView.DELETE.equals(cmd)) {
            String name = view.getSelectedPlayer();
            if (name == null) {
                JOptionPane.showMessageDialog(view, "Please select a player to delete.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!view.confirmPlayerDeletion(name)) {
                return;
            }
            try {
                gameManager.deletePlayerByName(name);
                SaveLoadService.saveGame(gameManager.getGameData());
                JOptionPane.showMessageDialog(view, "Player \"" + name + "\" deleted.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            } catch (GameException ex) {
                JOptionPane.showMessageDialog(view,
                        "Could not delete player: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
