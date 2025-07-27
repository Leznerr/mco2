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

    public void refresh() {
        String[] names = players.stream()
                .filter(p -> !"Bot".equalsIgnoreCase(p.getName()))
                .map(Player::getName)
                .toArray(String[]::new);
        view.setMerchantOptions(names);
        view.setClientOptions(names);
        view.resetDropdowns();
    }

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

    private void validateSelections() {
        String m = view.getSelectedMerchant();
        String c = view.getSelectedClient();
        boolean enabled = m != null && c != null && !m.equals(c);
        view.setStartTradingEnabled(enabled);
    }

    private void handleStartTrading() {
        String mName = view.getSelectedMerchant();
        String cName = view.getSelectedClient();
        if (mName == null || cName == null || mName.equals(cName)) {
            JOptionPane.showMessageDialog(view, "Select two different players.",
                    "Invalid Selection", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Player p1 = findPlayerByName(mName);
        Player p2 = findPlayerByName(cName);
        if (p1.getCharacters().isEmpty() || p2.getCharacters().isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "Both players must have at least one character to trade.",
                    "Invalid Selection", JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.println("TradingHallController starting trade between "
                + p1.getName() + " and " + p2.getName());
        view.dispose();
        sceneManager.showTradeView(p1, p2);
    }

    private Player findPlayerByName(String name) {
        return players.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new GameException("Player not found: " + name));
    }
}
