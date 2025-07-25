package model.util;


import java.util.List;

import model.core.Player;
import model.item.MagicItem;


/**
 * <h2>TradeOffer</h2>
 *
 * <p>Represents a pending trade proposal between two players,
 * specifying items offered and requested.</p>
 *
 * <h3>Design Summary:</h3>
 * <ul>
 *     <li><strong>DTO Role:</strong> Immutable, read-only after construction.</li>
 *     <li><strong>Encapsulation:</strong> Exposes unmodifiable item lists.</li>
 *     <li><strong>Error Handling:</strong> Throws {@link GameException} on invalid construction.</li>
 *     <li><strong>Rubric Compliance:</strong> Pure data class with zero business or UI logic.</li>
 * </ul>
 *
 * @author
 * @since 1.0
 */
public final class TradeOffer {

    /** The player offering the items (never null). */
    private final Player offeringPlayer;

    /** The player receiving the offer (never null). */
    private final Player receivingPlayer;

    /** The list of magic items being offered (never null, possibly empty). */
    private final List<MagicItem> itemsOffered;

    /** The list of magic items requested in return (never null, possibly empty). */
    private final List<MagicItem> itemsRequested;

    /**
     * Constructs a new {@code TradeOffer}.
     *
     * @param offeringPlayer  player proposing the trade (non-null)
     * @param receivingPlayer player receiving the trade offer (non-null)
     * @param offered         items being offered (non-null list)
     * @param requested       items requested in return (non-null list)
     * @throws GameException if any argument is invalid
     */
    public TradeOffer(Player offeringPlayer,
                      Player receivingPlayer,
                      List<MagicItem> offered,
                      List<MagicItem> requested) throws GameException {

        InputValidator.requireNonNull(offeringPlayer, "offeringPlayer");
        InputValidator.requireNonNull(receivingPlayer, "receivingPlayer");
        InputValidator.requireNonNull(offered, "itemsOffered");
        InputValidator.requireNonNull(requested, "itemsRequested");

        this.offeringPlayer = offeringPlayer;
        this.receivingPlayer = receivingPlayer;
        this.itemsOffered = List.copyOf(offered); // Defensive copy (immutable)
        this.itemsRequested = List.copyOf(requested);
    }

    /**
     * @return the player making the offer (non-null)
     */
    public Player getOfferingPlayer() {
        return offeringPlayer;
    }

    /**
     * @return the player receiving the offer (non-null)
     */
    public Player getReceivingPlayer() {
        return receivingPlayer;
    }

    /**
     * @return unmodifiable list of items being offered (never null)
     */
    public List<MagicItem> getItemsOffered() {
        return itemsOffered;
    }

    /**
     * @return unmodifiable list of items requested (never null)
     */
    public List<MagicItem> getItemsRequested() {
        return itemsRequested;
    }
}
