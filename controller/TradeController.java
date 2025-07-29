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
        view.refresh();
    }

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

    /**
     * Executes a multi-item trade between two characters.
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

    private void updateMerchantSelection() {
        Character c = view.getSelectedMerchantCharacter();
        if (c != null) {
            view.updateMerchantItems(c.getInventory().getAllItems());
        } else {
            view.updateMerchantItems(java.util.Collections.emptyList());
        }
        view.refresh();
    }

    private void updateClientSelection() {
        Character c = view.getSelectedClientCharacter();
        if (c != null) {
            view.updateClientItems(c.getInventory().getAllItems());
        } else {
            view.updateClientItems(java.util.Collections.emptyList());
        }
        view.refresh();
    }

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

    private String itemNames(List<MagicItem> items) {
        return items.stream().map(MagicItem::getName).collect(java.util.stream.Collectors.joining(", "));
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

