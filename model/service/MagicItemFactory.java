package model.service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import model.item.MagicItem;
import model.item.PassiveItem;
import model.item.RarityType;
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
        new SingleUseItem(
                "Potion of Minor Healing",
                "Drink to instantly restore 40 HP. Brewed by caravan apprentices.",
                RarityType.COMMON,
                SingleUseEffectType.HEAL_HP,
                40),
        new SingleUseItem(
                "Scroll of Minor Energy",
                "A hastily scribed scroll that replenishes 20 EP when read aloud.",
                RarityType.COMMON,
                SingleUseEffectType.RESTORE_EP,
                20),
        new SingleUseItem(
                "Defender's Aegis",
                "Envelops the bearer in a barrier, negating all damage for one turn.",
                RarityType.COMMON,
                SingleUseEffectType.GRANT_IMMUNITY,
                1)
    );

    private static final List<MagicItem> UNCOMMON_ITEMS = List.of(
        new PassiveItem(
                "Amulet of Vitality",
                "An emerald charm that raises maximum HP by 20 while worn.",
                RarityType.UNCOMMON),
        new PassiveItem(
                "Ring of Focus",
                "Favored by Arcane College scholars. Grants +2 EP each turn.",
                RarityType.UNCOMMON)
    );

    private static final List<MagicItem> RARE_ITEMS = List.of(
        new PassiveItem(
                "Orb of Resilience",
                "A mysterious sphere that heals 5 HP at the start of each turn.",
                RarityType.RARE),
        new PassiveItem(
                "Ancient Tome of Power",
                "Dusty pages filled with forgotten spells. Gain +5 EP each turn.",
                RarityType.RARE)
    );

    /* -------------------------------------------------------------
     * Rarity Weighting (Cumulative Ranges)
     * ----------------------------------------------------------- */

    private static final int COMMON_UPPER   = RarityType.COMMON.getDropChance();
    private static final int UNCOMMON_UPPER = COMMON_UPPER + RarityType.UNCOMMON.getDropChance();
    // Remaining percentage belongs to RARE

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
        int roll = random.nextInt(100) + 1; // 1–100
        RarityType rarity = (roll <= COMMON_UPPER)   ? RarityType.COMMON
                             : (roll <= UNCOMMON_UPPER) ? RarityType.UNCOMMON
                                                        : RarityType.RARE;
        return getRandomItemByRarity(rarity, random);
    }

    /** Returns a random item template from the given rarity pool. */
    public static MagicItem getRandomItemByRarity(RarityType rarity, Random random) {
        Objects.requireNonNull(rarity, "rarity");
        Objects.requireNonNull(random, "random");

        List<MagicItem> pool = switch (rarity) {
            case COMMON -> COMMON_ITEMS;
            case UNCOMMON -> UNCOMMON_ITEMS;
            case RARE -> RARE_ITEMS;
        };
        MagicItem template = pool.get(random.nextInt(pool.size()));
        return template.copy();
    }
}
