package controller;

import model.core.Character;
import model.core.Player;
import model.item.MagicItem;
import model.util.GameException;
import model.util.InputValidator;
import persistence.GameData;
import persistence.SaveLoadService;
import view.TradeView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
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

    // ------------------------------------------------------------------
    // Core business logic
    // ------------------------------------------------------------------

    public List<Character> listEligibleCharacters(Character self) throws GameException {
        InputValidator.requireNonNull(self, "self");
        List<Character> result = new ArrayList<>();
        for (Player p : players) {
            for (Character c : p.getCharacters()) {
                if (!c.equals(self) && !isBot(c)) {
                    result.add(c);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    public List<MagicItem> getInventory(Character character) throws GameException {
        InputValidator.requireNonNull(character, "character");
        return character.getInventory().getAllItems();
    }

    public void executeTrade(Character a, MagicItem aItem,
                             Character b, MagicItem bItem) throws GameException {
        InputValidator.requireNonNull(a, "character A");
        InputValidator.requireNonNull(b, "character B");
        InputValidator.requireNonNull(aItem, "character A item");
        InputValidator.requireNonNull(bItem, "character B item");

        if (a.equals(b)) {
            throw new GameException("Cannot trade items with oneself.");
        }
        if (isBot(a) || isBot(b)) {
            throw new GameException("Trading with bots is not allowed.");
        }
        if (!a.getInventory().getAllItems().contains(aItem)) {
            throw new GameException(a.getName() + " does not possess " + aItem.getName());
        }
        if (!b.getInventory().getAllItems().contains(bItem)) {
            throw new GameException(b.getName() + " does not possess " + bItem.getName());
        }

        a.getInventory().removeItem(aItem);
        b.getInventory().addItem(aItem);

        b.getInventory().removeItem(bItem);
        a.getInventory().addItem(bItem);
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
            InputValidator.requireNonNull(c1, "Character A");
            InputValidator.requireNonNull(c2, "Character B");

            List<MagicItem> sel1 = new ArrayList<>(view.getSelectedItems1());
            List<MagicItem> sel2 = new ArrayList<>(view.getSelectedItems2());

            if (sel1.isEmpty() || sel2.isEmpty()) {
                view.showError("Select one item from each character to trade.");
                return;
            }

            MagicItem m1 = sel1.get(0);
            MagicItem m2 = sel2.get(0);

            executeTrade(c1, m1, c2, m2);

            persist();
            view.showInfo("Trade completed successfully.");
            view.refreshLists();
        } catch (GameException ex) {
            view.showError(ex.getMessage());
        }
    }

    private boolean isBot(Character c) {
        return c.getName().equalsIgnoreCase("Bot");
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

