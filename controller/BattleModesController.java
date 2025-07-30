package controller;

import view.BattleModesView;
import model.core.Player;
import controller.GameManagerController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/** Controller for selecting battle mode (PvP or PvB). */
public class BattleModesController implements ActionListener {
    private final BattleModesView view;
    private final List<Player> players;
    private final SceneManager sceneManager;
    private final GameManagerController gameManagerController;

    /**
     * Constructs a new battle mode selection controller.
     *
     * @param view the view associated with the battle mode selection screen
     * @param players the list of players involved
     * @param sceneManager handles scene switching logic
     * @param gm the main game manager controller
     */
    public BattleModesController(BattleModesView view,
                                 List<Player> players,
                                 SceneManager sceneManager,
                                 GameManagerController gm) {
        this.view = view;
        this.players = players;
        this.sceneManager = sceneManager;
        this.gameManagerController = gm;
        this.view.setActionListener(this);
    }

    /**
     * Handles user input events such as mode selection or returning to the menu.
     *
     * @param e the action event triggered by a button press
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case BattleModesView.PLAYER_VS_PLAYER -> handlePvP();
            case BattleModesView.PLAYER_VS_BOT -> handlePvB();
            case BattleModesView.RETURN -> {
                view.dispose();
                gameManagerController.navigateBackToMainMenu();
            }
        }
    }

    /**
     * Handles logic for starting a Player vs Player battle.
     * Validates that at least two players are available.
     */
    private void handlePvP() {
        if (players.size() < 2) {
            JOptionPane.showMessageDialog(view, "Two players required for PvP.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            view.dispose();
            new BattleSetupController(sceneManager, players)
                    .startPvP();
        }
    }

    /**
     * Handles logic for starting a Player vs Bot battle.
     * Validates that at least one player is available.
     */
    private void handlePvB() {
        if (players.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No players registered.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            view.dispose();
            new BattleSetupController(sceneManager, players)
                    .startPvB();
        }
    }
}
