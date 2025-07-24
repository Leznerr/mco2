package model.service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import model.item.ItemType;
import model.item.MagicItem;
import model.item.PassiveItem;
import model.item.SingleUseItem;

/**
 * <h2>MagicItemFactory</h2>
 * <p>Static utility class that randomly creates {@link MagicItem} rewards,
 * observing the specified rarity distribution (Common 70%, Uncommon 25%, Rare 5%).</p>
 *
 * <p><strong>Design Features:</strong></p>
 * <ul>
 *   <li><strong>Singleton/Utility:</strong> Non-instantiable class with only static methods</li>
 *   <li><strong>Immutability:</strong> Reward pools are final and unmodifiable</li>
 *   <li><strong>Thread-Safe:</strong> Uses {@link SecureRandom} safely</li>
 *   <li><strong>Testable:</strong> Overload allows injecting a deterministic {@link Random}</li>
 * </ul>
 */
public final class MagicItemFactory {

    /* -------------------------------------------------------------
     * Rarity Pools (Immutable Lists)
     * ----------------------------------------------------------- */

    private static final List<MagicItem> COMMON_ITEMS = List.of(
        new SingleUseItem("Minor Healing Potion", "Restore 25 HP.", "Common"),
        new PassiveItem("Copper Ring", "Max EP +5.", "Common")
    );

    private static final List<MagicItem> UNCOMMON_ITEMS = List.of(
        new SingleUseItem("Elixir of Focus", "Restore 15 EP.", "Uncommon"),
        new PassiveItem("Silver Amulet", "Max HP +15.", "Uncommon")
    );

    private static final List<MagicItem> RARE_ITEMS = List.of(
        new SingleUseItem("Phoenix Tear", "Revive from KO with 50% HP.", "Rare"),
        new PassiveItem("Golden Dragon Scale", "Defense +10%.", "Rare")
    );

    /* -------------------------------------------------------------
     * Rarity Weighting (Cumulative Ranges)
     * ----------------------------------------------------------- */

    private static final int COMMON_UPPER   = 69; // 0–69
    private static final int UNCOMMON_UPPER = 94; // 70–94
    // 95–99 → Rare

    private static final SecureRandom RNG = new SecureRandom();

    /**
     * Private constructor to prevent instantiation.
     * Enforces static utility class pattern.
     */
    private MagicItemFactory() {
        throw new AssertionError("Utility class – do not instantiate.");
    }

    /* -------------------------------------------------------------
     * Public Factory Methods
     * ----------------------------------------------------------- */

    /**
     * Creates a new random {@link MagicItem} reward using secure random seed.
     *
     * @return newly copied reward item (never shared state)
     */
    public static MagicItem createRandomReward() {
        return createRandomReward(RNG);
    }

    /**
     * Creates a new random {@link MagicItem} reward using the given PRNG.
     *
     * <p>This is used for predictable testing.</p>
     *
     * @param random non-null pseudo-random generator
     * @return deep copy of selected reward
     * @throws NullPointerException if {@code random} is null
     */
    public static MagicItem createRandomReward(Random random) {
        Objects.requireNonNull(random, "Random generator must not be null");

        int roll = random.nextInt(100); // 0–99
        List<MagicItem> pool = (roll <= COMMON_UPPER)   ? COMMON_ITEMS
                             : (roll <= UNCOMMON_UPPER) ? UNCOMMON_ITEMS
                                                        : RARE_ITEMS;

        MagicItem template = pool.get(random.nextInt(pool.size()));
        return template.copy(); // ensures deep-copy, no shared state
    }
}
