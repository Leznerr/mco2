package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.core.Character;
import model.core.Player;
import model.item.Inventory;
import model.item.MagicItem;
import model.util.GameException;
import model.util.InputValidator;
import persistence.GameData;
import persistence.SaveLoadService;
import view.TradeView;

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

    /**
     * Convenience constructor wiring the view immediately.
     */
    public TradeController(TradeView view, List<Player> players) throws GameException {
        this(players);
        setView(view);
    }

    /**
     * Binds a {@link TradeView} to this controller. 
     * <p>
     * Sets up the action listener and triggers initial UI refresh.
     *
     * @param view the trade view to associate
     * @throws GameException if the view is null
     */
    public void setView(TradeView view) throws GameException {
        InputValidator.requireNonNull(view, "view");
        this.view = view;
        view.setActionListener(this);
        view.refresh();
    }

    /**
     * Handles user actions for player trades.
     *
     * @param e the action event triggered by a UI component
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (TradeView.TRADE.equals(cmd)) {
            handleTrade();
        } else if (TradeView.RETURN.equals(cmd)) {
            view.dispose();
        } else if (TradeView.MERCHANT_SELECT.equals(cmd)) {
            updateMerchantSelection();
        } else if (TradeView.CLIENT_SELECT.equals(cmd)) {
            updateClientSelection();
        }
    }

    // ------------------------------------------------------------------
    // Public API used by unit tests and UI layer
    // ------------------------------------------------------------------

    /**
     * Returns an unmodifiable list of all eligible (non-bot) players.
     *
     * @return list of players excluding bots
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
     * Retrieves all characters associated with the specified player.
     *
     * @param player the player whose characters to fetch
     * @return list of characters
     * @throws GameException if {@code player} is null
     */
    public List<Character> getCharactersForPlayer(Player player) throws GameException {
        InputValidator.requireNonNull(player, "player");
        return Collections.unmodifiableList(player.getCharacters());
    }

    /**
     * Returns a read-only list of items in the character's inventory.
     *
     * @param character the character to inspect
     * @return unmodifiable list of magic items
     * @throws GameException if {@code character} is null
     */
    public List<MagicItem> getInventory(Character character) throws GameException {
        InputValidator.requireNonNull(character, "character");
        return Collections.unmodifiableList(character.getInventory().getAllItems());
    }

    /**
     * Executes a one-for-one item trade between two characters.
     * <p>
     * Validates ownership and prevents self-trading or bot involvement.
     *
     * @param source character giving away {@code itemFromSource}
     * @param itemFromSource item from {@code source}
     * @param target character giving away {@code itemFromTarget}
     * @param itemFromTarget item from {@code target}
     * @throws GameException on invalid ownership or other rule violation
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

    /**
     * Executes a multi-item trade between two characters.
     * <p>
     * Validates ownership, excludes bots, unequips traded equipped items,
     * and prevents self-trades or empty trades.
     *
     * @param source character trading away {@code itemsFromSource}
     * @param itemsFromSource items to give from {@code source}
     * @param target character trading away {@code itemsFromTarget}
     * @param itemsFromTarget items to give from {@code target}
     * @throws GameException on validation failure
     */
    public void executeTrade(Character source,
                             List<MagicItem> itemsFromSource,
                             Character target,
                             List<MagicItem> itemsFromTarget) throws GameException {

        InputValidator.requireNonNull(source, "source");
        InputValidator.requireNonNull(target, "target");
        InputValidator.requireNonNull(itemsFromSource, "itemsFromSource");
        InputValidator.requireNonNull(itemsFromTarget, "itemsFromTarget");

        if (source == target) {
            throw new GameException("Cannot trade items with oneself.");
        }

        Player p1 = findPlayerForCharacter(source);
        Player p2 = findPlayerForCharacter(target);
        if (isBot(p1) || isBot(p2)) {
            throw new GameException("Trading with bots is not allowed.");
        }

        if (itemsFromSource.isEmpty() && itemsFromTarget.isEmpty()) {
            throw new GameException("No items selected for trade.");
        }

        Inventory inv1 = source.getInventory();
        Inventory inv2 = target.getInventory();

        if (!inv1.getAllItems().containsAll(itemsFromSource) ||
            !inv2.getAllItems().containsAll(itemsFromTarget)) {
            throw new GameException("Selected items must belong to the chosen characters.");
        }

        for (MagicItem m : itemsFromSource) {
            inv1.removeItem(m);
            if (source.getEquippedItem() == m) {
                source.unequipItem();
            }
        }
        for (MagicItem m : itemsFromTarget) {
            inv2.removeItem(m);
            if (target.getEquippedItem() == m) {
                target.unequipItem();
            }
        }

        for (MagicItem m : itemsFromSource) {
            inv2.addItem(m);
        }
        for (MagicItem m : itemsFromTarget) {
            inv1.addItem(m);
        }
    }

    /** Determines if the given player represents an AI bot. */
    private boolean isBot(Player p) {
        return p != null && "Bot".equalsIgnoreCase(p.getName());
    }

    /**
     * Handles trade logic triggered by the user.
     * <p>
     * Validates character selection, performs the exchange, updates the
     * persistent state, logs the result, and refreshes the view.
     */
    private void handleTrade() {
        Character merchant = view.getSelectedMerchantCharacter();
        Character client = view.getSelectedClientCharacter();
        List<MagicItem> mItems = view.getSelectedMerchantItems();
        List<MagicItem> cItems = view.getSelectedClientItems();
        if (merchant == null || client == null || merchant == client) {
            view.showError("Select two different characters.");
        } else if (mItems.isEmpty() && cItems.isEmpty()) {
            view.showError("Select at least one item to trade.");
        } else {
            try {
                executeTrade(merchant, mItems, client, cItems);
                persist();
                view.appendTradeLog(buildLogMessage(merchant, mItems, client, cItems));
                view.showInfo("Trade completed successfully.");
                view.refresh();
            } catch (GameException ex) {
                view.showError(ex.getMessage());
            }
        }
    }

    /**
     * Updates the merchant item list in the UI based on the selected character.
     */
    private void updateMerchantSelection() {
        Character c = view.getSelectedMerchantCharacter();
        if (c != null) {
            view.updateMerchantItems(c.getInventory().getAllItems());
        } else {
            view.updateMerchantItems(java.util.Collections.emptyList());
        }
        view.refresh();
    }

    /**
     * Updates the client item list in the UI based on the selected character.
     */
    private void updateClientSelection() {
        Character c = view.getSelectedClientCharacter();
        if (c != null) {
            view.updateClientItems(c.getInventory().getAllItems());
        } else {
            view.updateClientItems(java.util.Collections.emptyList());
        }
        view.refresh();
    }

    /**
     * Builds a human-readable trade summary for logging purposes.
     *
     * @param m merchant character
     * @param mItems merchant items traded
     * @param c client character
     * @param cItems client items traded
     * @return formatted log string
     */
    private String buildLogMessage(Character m, List<MagicItem> mItems,
                                   Character c, List<MagicItem> cItems) {
        StringBuilder sb = new StringBuilder();
        if (!mItems.isEmpty()) {
            sb.append(m.getName()).append(" -> ").append(c.getName()).append(": ");
            sb.append(itemNames(mItems));
        }
        if (!cItems.isEmpty()) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append(c.getName()).append(" -> ").append(m.getName()).append(": ");
            sb.append(itemNames(cItems));
        }
        return sb.toString();
    }

    /**
     * Joins a list of item names into a comma-separated string.
     *
     * @param items list of magic items
     * @return name list
     */
    private String itemNames(List<MagicItem> items) {
        return items.stream().map(MagicItem::getName).collect(java.util.stream.Collectors.joining(", "));
    }

    /**
     * Finds the owning player of a given character.
     *
     * @param c character to search for
     * @return player owning the character
     * @throws GameException if character is unassigned
     */
    private Player findPlayerForCharacter(Character c) throws GameException {
        for (Player p : players) {
            if (p.getCharacters().contains(c)) {
                return p;
            }
        }
        throw new GameException("Character does not belong to any loaded player.");
    }

    /**
     * Saves updated player data to persistent storage.
     *
     * @throws GameException on save failure
     */
    private void persist() throws GameException {
        GameData data = SaveLoadService.loadGame();
        data.setAllPlayers(players);
        SaveLoadService.saveGame(data);
    }
}

