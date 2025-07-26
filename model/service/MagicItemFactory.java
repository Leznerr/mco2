package model.service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import model.item.MagicItem;
import model.item.PassiveItem;
import model.item.SingleUseItem;
import model.item.SingleUseEffectType;

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
        new SingleUseItem("Potion of Minor Healing", "Heals 40 HP.", "Common",
                SingleUseEffectType.HEAL_HP, 40),
        new SingleUseItem("Scroll of Minor Energy", "Restores 20 EP.", "Common",
                SingleUseEffectType.RESTORE_EP, 20),
        new SingleUseItem("Defender's Aegis", "Negates all damage for one turn.", "Common",
                SingleUseEffectType.GRANT_IMMUNITY, 1)
    );

    private static final List<MagicItem> UNCOMMON_ITEMS = List.of(
        new PassiveItem("Amulet of Vitality", "Max HP +20 while equipped.", "Uncommon"),
        new PassiveItem("Ring of Focus", "+2 EP each turn.", "Uncommon")
    );

    private static final List<MagicItem> RARE_ITEMS = List.of(
        new PassiveItem("Orb of Resilience", "Heal +5 HP each turn.", "Rare"),
        new PassiveItem("Ancient Tome of Power", "+5 EP each turn.", "Rare")
    );

    /* -------------------------------------------------------------
     * Rarity Weighting (Cumulative Ranges)
     * ----------------------------------------------------------- */

    private static final int COMMON_UPPER   = 59; // 0–59
    private static final int UNCOMMON_UPPER = 94; // 60–94
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
