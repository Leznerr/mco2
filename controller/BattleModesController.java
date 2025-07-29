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
