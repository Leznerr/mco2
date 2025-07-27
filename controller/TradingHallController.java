package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;

import model.core.Player;
import model.util.GameException;
import model.util.InputValidator;
import view.TradeView;
import view.TradingHallView;

/**
 * Controller for the Trading Hall screen. Handles player selection
 * and launches the {@link TradeView} for the chosen pair.
 */
public class TradingHallController implements ActionListener {

    private final TradingHallView view;
    private final List<Player> players;

    public TradingHallController(TradingHallView view, List<Player> players) throws GameException {
        InputValidator.requireNonNull(view, "view");
        InputValidator.requireNonNull(players, "players");
        this.view = view;
        this.players = players;
        this.view.setActionListener(this);
        refreshOptions();
    }

    private void refreshOptions() {
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
        if (TradingHallView.RETURN_TO_MENU.equals(cmd)) {
            view.dispose();
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
        TradeView tv = new TradeView(p1, p2);
        try {
            new TradeController(tv, players);
        } catch (GameException ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            tv.dispose();
        }
    }

    private Player findPlayerByName(String name) {
        return players.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new GameException("Player not found: " + name));
    }
}
