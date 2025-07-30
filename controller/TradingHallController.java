package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;

import model.core.Player;
import model.util.GameException;
import model.util.InputValidator;
import view.TradingHallView;
import controller.SceneManager;

/**
 * Controller for the Trading Hall screen. Handles player selection
 * and launches the trading view for the chosen pair via {@link SceneManager}.
 */
public class TradingHallController implements ActionListener {

    private final TradingHallView view;
    private final List<Player> players;
    private final SceneManager sceneManager;

    /**
     * Constructs the TradingHallController, initializing the view and players, 
     * and setting up the action listener and dropdown data.
     *
     * @param view the trading hall view to control
     * @param players the list of players available for trading
     * @param sceneManager the scene manager to switch between views
     * @throws GameException if any parameter is null
     */
    public TradingHallController(TradingHallView view, List<Player> players, SceneManager sceneManager) throws GameException {
        InputValidator.requireNonNull(view, "view");
        InputValidator.requireNonNull(players, "players");
        InputValidator.requireNonNull(sceneManager, "sceneManager");
        this.view = view;
        this.players = players;
        this.sceneManager = sceneManager;
        this.view.setActionListener(this);
        refresh();
    }

    /**
     * Refreshes the merchant and client dropdown options by excluding bots
     * and resetting the dropdown selections.
     */
    public void refresh() {
        String[] names = players.stream()
                .filter(p -> !"Bot".equalsIgnoreCase(p.getName()))
                .map(Player::getName)
                .toArray(String[]::new);
        view.setMerchantOptions(names);
        view.setClientOptions(names);
        view.resetDropdowns();
    }

    /**
     * Handles UI actions from the TradingHallView.
     * Supports returning to the main menu or initiating a trade,
     * and updates button states on dropdown interaction.
     *
     * @param e the triggered action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        System.out.println("TradingHallController action: " + cmd);
        if (TradingHallView.RETURN_TO_MENU.equals(cmd)) {
            view.dispose();
            sceneManager.showMainMenu();
        } else if (TradingHallView.START_TRADING.equals(cmd)) {
            handleStartTrading();
        } else {
            // update button state on dropdown changes
            validateSelections();
        }
    }

    /**
     * Validates the merchant and client selections to determine
     * whether the "Start Trading" button should be enabled.
     * It ensures both players are selected and are not the same.
     */
    private void validateSelections() {
        String m = view.getSelectedMerchant();
        String c = view.getSelectedClient();
        boolean enabled = m != null && c != null && !m.equals(c);
        view.setStartTradingEnabled(enabled);
    }

    /**
     * Handles logic for initiating a trade session between two players.
     * Verifies that both players are selected, distinct, and have characters.
     * Proceeds to trade view if valid, otherwise shows an error dialog.
     */
    private void handleStartTrading() {
        String mName = view.getSelectedMerchant();
        String cName = view.getSelectedClient();
        if (mName == null || cName == null || mName.equals(cName)) {
            JOptionPane.showMessageDialog(view, "Select two different players.",
                    "Invalid Selection", JOptionPane.ERROR_MESSAGE);
        } else {
            Player p1 = findPlayerByName(mName);
            Player p2 = findPlayerByName(cName);
            if (p1.getCharacters().isEmpty() || p2.getCharacters().isEmpty()) {
                JOptionPane.showMessageDialog(view,
                        "Both players must have at least one character to trade.",
                        "Invalid Selection", JOptionPane.ERROR_MESSAGE);
            } else {
                System.out.println("TradingHallController starting trade between "
                        + p1.getName() + " and " + p2.getName());
                view.dispose();
                sceneManager.showTradeView(p1, p2);
            }
        }
    }

    /**
     * Finds a player by their name (case-insensitive).
     *
     * @param name the name of the player to find
     * @return the matching Player instance
     * @throws GameException if no player with the given name is found
     */
    private Player findPlayerByName(String name) {
        return players.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new GameException("Player not found: " + name));
    }
}
