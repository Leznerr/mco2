package controller;

import model.core.Character;
import model.core.Player;
import model.item.MagicItem;
import model.util.GameException;
import model.util.InputValidator;
import model.util.TradeOffer;
import persistence.GameData;
import persistence.SaveLoadService;
import view.TradeView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller handling magic item trading between players.
 * <p>
 * Validates selections, performs the exchange, and saves
 * updated player data via {@link SaveLoadService}.
 */
public class TradeController implements ActionListener {

    private final TradeView view;
    private final List<Player> players;

    public TradeController(TradeView view, List<Player> players) throws GameException {
        InputValidator.requireNonNull(view, "view");
        InputValidator.requireNonNull(players, "players");
        this.view = view;
        this.players = players;
        view.setActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (TradeView.TRADE.equals(cmd)) {
            handleTrade();
        } else if (TradeView.CANCEL.equals(cmd)) {
            view.dispose();
        }
    }

    private void handleTrade() {
        try {
            Character c1 = view.getSelectedChar1();
            Character c2 = view.getSelectedChar2();
            InputValidator.requireNonNull(c1, "Offering character");
            InputValidator.requireNonNull(c2, "Receiving character");

            List<MagicItem> offered = new ArrayList<>(view.getSelectedItems1());
            List<MagicItem> requested = new ArrayList<>(view.getSelectedItems2());

            // Validate ownership
            if (!c1.getInventory().getAllItems().containsAll(offered) ||
                !c2.getInventory().getAllItems().containsAll(requested)) {
                view.showError("Invalid item selection for trade.");
                return;
            }

            Player p1 = findPlayerForCharacter(c1);
            Player p2 = findPlayerForCharacter(c2);

            // Construct offer for record/possible future use
            new TradeOffer(p1, p2, offered, requested);

            // Execute trade
            for (MagicItem m : offered) {
                c1.getInventory().removeItem(m);
                c2.getInventory().addItem(m);
            }
            for (MagicItem m : requested) {
                c2.getInventory().removeItem(m);
                c1.getInventory().addItem(m);
            }

            persist();
            view.showInfo("Trade completed successfully.");
            view.refreshLists();
        } catch (GameException ex) {
            view.showError(ex.getMessage());
        }
    }

    private Player findPlayerForCharacter(Character c) throws GameException {
        for (Player p : players) {
            if (p.getCharacters().contains(c)) {
                return p;
            }
        }
        throw new GameException("Character does not belong to any loaded player.");
    }

    private void persist() {
        try {
            GameData data = SaveLoadService.loadGame();
            data.setAllPlayers(players);
            SaveLoadService.saveGame(data);
        } catch (GameException e) {
            JOptionPane.showMessageDialog(view, "Failed to save game: " + e.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

