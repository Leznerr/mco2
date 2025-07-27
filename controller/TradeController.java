package controller;

import model.core.Character;
import model.core.Player;
import model.item.Inventory;
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

    private TradeView view; // optional view for UI integration
    private final List<Player> players;

    /**
     * Constructs a controller for the given players. The view may be supplied
     * later via {@link #setView(TradeView)} for GUI integration.
     *
     * @param players list of players participating in trading (non-null)
     * @throws GameException if {@code players} is {@code null}
     */
    public TradeController(List<Player> players) throws GameException {
        InputValidator.requireNonNull(players, "players");
        this.players = players;
    }

    /** Convenience constructor wiring the view immediately. */
    public TradeController(TradeView view, List<Player> players) throws GameException {
        this(players);
        setView(view);
    }

    /**
     * Binds a {@link TradeView} to this controller. Primarily used by the GUI
     * layer but optional for unit testing.
     */
    public void setView(TradeView view) throws GameException {
        InputValidator.requireNonNull(view, "view");
        this.view = view;
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

    // ------------------------------------------------------------------
    // Public API used by unit tests and UI layer
    // ------------------------------------------------------------------

    /**
     * Returns an immutable list of players that can participate in trading.
     * Bots (players named "Bot") are excluded.
     */
    public List<Player> getEligiblePlayers() {
        List<Player> eligible = new ArrayList<>();
        for (Player p : players) {
            if (!isBot(p)) {
                eligible.add(p);
            }
        }
        return Collections.unmodifiableList(eligible);
    }

    /**
     * Returns all characters for the specified player.
     */
    public List<Character> getCharactersForPlayer(Player player) throws GameException {
        InputValidator.requireNonNull(player, "player");
        return Collections.unmodifiableList(player.getCharacters());
    }

    /**
     * Returns a read-only list of all items owned by the character.
     */
    public List<MagicItem> getInventory(Character character) throws GameException {
        InputValidator.requireNonNull(character, "character");
        return Collections.unmodifiableList(character.getInventory().getAllItems());
    }

    /**
     * Executes an item-for-item trade between two characters.
     *
     * @param source      character giving {@code itemFromSource}
     * @param itemFromSource item owned by {@code source}
     * @param target      character giving {@code itemFromTarget}
     * @param itemFromTarget item owned by {@code target}
     * @throws GameException if validation fails
     */
    public void executeTrade(Character source,
                             MagicItem itemFromSource,
                             Character target,
                             MagicItem itemFromTarget) throws GameException {

        InputValidator.requireNonNull(source, "source");
        InputValidator.requireNonNull(target, "target");
        InputValidator.requireNonNull(itemFromSource, "itemFromSource");
        InputValidator.requireNonNull(itemFromTarget, "itemFromTarget");

        if (source == target) {
            throw new GameException("Cannot trade items with oneself.");
        }

        Player p1 = findPlayerForCharacter(source);
        Player p2 = findPlayerForCharacter(target);

        if (isBot(p1) || isBot(p2)) {
            throw new GameException("Trading with bots is not allowed.");
        }

        Inventory inv1 = source.getInventory();
        Inventory inv2 = target.getInventory();

        if (!inv1.getAllItems().contains(itemFromSource) ||
            !inv2.getAllItems().contains(itemFromTarget)) {
            throw new GameException("Selected items must belong to the chosen characters.");
        }

        inv1.removeItem(itemFromSource);
        inv2.removeItem(itemFromTarget);

        inv1.addItem(itemFromTarget);
        inv2.addItem(itemFromSource);
    }

    /** Determines if the given player represents an AI bot. */
    private boolean isBot(Player p) {
        return p != null && "Bot".equalsIgnoreCase(p.getName());
    }

    private void handleTrade() {
        boolean executed = false;
        MagicItem m1 = null;
        MagicItem m2 = null;
        Character c1 = null;
        Character c2 = null;
        try {
            c1 = view.getSelectedChar1();
            c2 = view.getSelectedChar2();
            InputValidator.requireNonNull(c1, "Offering character");
            InputValidator.requireNonNull(c2, "Receiving character");

            m1 = view.getSelectedItem1();
            m2 = view.getSelectedItem2();
            if (m1 == null || m2 == null) {
                view.showError("Select an item from each character.");
                return;
            }
            executeTrade(c1, m1, c2, m2);
            executed = true;

            persist();
            view.showInfo("Trade completed successfully.");
            view.refreshLists();
        } catch (GameException ex) {
            if (executed) {
                // revert trade on failure
                try {
                    // swap back
                    executeTrade(c1, m2, c2, m1);
                } catch (GameException ignore) {
                    // ignore to avoid masking original error
                }
            }
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

    private void persist() throws GameException {
        GameData data = SaveLoadService.loadGame();
        data.setAllPlayers(players);
        SaveLoadService.saveGame(data);
    }
}

