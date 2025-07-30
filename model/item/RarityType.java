package model.item;

/**
 * Enumerates item rarity tiers and their drop chances.
 */
public enum RarityType {
    /** Common items drop 60% of the time. */
    COMMON(60),
    /** Uncommon items drop 35% of the time. */
    UNCOMMON(35),
    /** Rare items drop 5% of the time. */
    RARE(5);

    private final int dropChance;

    /**
     * Constructs a rarity type with the specified drop chance percentage.
     *
     * @param chance the drop chance associated with this rarity tier
     */
    RarityType(int chance) {
        this.dropChance = chance;
    }

    /** @return the percentage drop chance for this rarity */
    public int getDropChance() {
        return dropChance;
    }

    /**
     * Returns a capitalized version of the enum name (e.g., "Common" instead of "COMMON").
     *
     * @return a human-readable string representing the rarity type
     */
    @Override
    public String toString() {
        String n = name().toLowerCase();
        return Character.toUpperCase(n.charAt(0)) + n.substring(1);
    }
}
