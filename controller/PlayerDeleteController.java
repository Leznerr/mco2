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
 * Controller handling deletion of players from the game.
 */
public class PlayerDeleteController implements ActionListener {

    private final PlayerDeleteView view;
    private final GameManagerController gameManager;
    private final SceneManager sceneManager;

    public PlayerDeleteController(PlayerDeleteView view, GameManagerController gameManager, SceneManager sceneManager) {
        this.view = view;
        this.gameManager = gameManager;
        this.sceneManager = sceneManager;
        this.view.setActionListener(this);
        refresh();
    }

    /** Refreshes dropdown and list of players. */
    void refresh() {
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
            sceneManager.showPlayerRegistration();
        } else if (PlayerDeleteView.DELETE.equals(cmd)) {
            String name = view.getSelectedPlayer();
            boolean proceed = true;
            if (name == null) {
                JOptionPane.showMessageDialog(view,
                        "Please select a player to delete.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                proceed = false;
            } else if (!view.confirmPlayerDeletion(name)) {
                proceed = false;
            }

            if (proceed) {
                try {
                    gameManager.deletePlayerByName(name);
                    SaveLoadService.saveGame(gameManager.getGameData());
                    JOptionPane.showMessageDialog(view,
                            "Player \"" + name + "\" deleted.",
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
}
