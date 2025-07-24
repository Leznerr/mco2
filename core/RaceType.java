package model.core;


import java.util.Arrays;

/**
 * {@code RaceType} enumerates all playable races in <i>Fatal Fantasy: Tactics</i>.
 * Each constant defines unique stat bonuses via a {@link RaceBonus}, which are applied
 * to a {@link Character} during creation.
 *
 * <h3>Specification Traceability</h3>
 * <ul>
 * <li><strong>MCO2 – Race System:</strong> A race selection step must be added during character creation.</li>
 * </ul>
 *
 * <p>Enum values are linked to immutable {@link RaceBonus} objects and user-facing
 * descriptions to guide players in the UI.</p>
 */
public enum RaceType {

    /**
     * Adaptable and resilient, humans possess a balanced set of attributes.
     * <ul>
     * <li>HP Bonus: +15</li>
     * <li>EP Bonus: +5</li>
     * <li>Ability Slots: 0</li>
     * </ul>
     */
    HUMAN(
        "Adaptable and resilient, humans possess a balanced set of attributes.",
        new RaceBonus(15, 5, 0)
    ),

    /**
     * Stocky and tough, dwarves are known for their incredible endurance.
     * <ul>
     * <li>HP Bonus: +30</li>
     * <li>EP Bonus: 0</li>
     * <li>Ability Slots: 0</li>
     * </ul>
     */
    DWARF(
        "Stocky and tough, dwarves are known for their incredible endurance and steadfastness.",
        new RaceBonus(30, 0, 0)
    ),

    /**
     * Graceful and naturally attuned to arcane energies.
     * <ul>
     * <li>HP Bonus: 0</li>
     * <li>EP Bonus: +15</li>
     * <li>Ability Slots: 0</li>
     * </ul>
     */
    ELF(
        "Graceful and naturally attuned to arcane energies, elves excel in magical prowess.",
        new RaceBonus(0, 15, 0)
    ),

    /**
     * Clever and resourceful, with a knack for unusual tricks.
     * <ul>
     * <li>HP Bonus: 0</li>
     * <li>EP Bonus: 0</li>
     * <li>Ability Slots: +1</li>
     * </ul>
     */
    GNOME(
        "Clever and resourceful, gnomes have a knack for finding hidden opportunities.",
        new RaceBonus(0, 0, 1)
    );

    private final String description;
    private final RaceBonus raceBonus;

    /**
     * Constructs a race definition.
     *
     * @param description user-facing explanation (non-blank)
     * @param raceBonus   stat bonuses for this race (non-null)
     */
    RaceType(String description, RaceBonus raceBonus) {
        // Validation for constructor parameters can be added here if needed
        this.description = description;
        this.raceBonus = raceBonus;
    }

    /**
     * Returns a human-readable race description used in the GUI.
     *
     * @return non-blank description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the immutable stat bonuses granted by this race.
     *
     * @return a {@link RaceBonus} object
     */
    public RaceBonus getRaceBonus() {
        return raceBonus;
    }

    /**
     * Convenience method to get the Health Point (HP) bonus for this race.
     * @return The HP bonus.
     */
    public int getHpBonus() {
        return this.raceBonus.getHpBonus();
    }

    /**
     * Convenience method to get the Energy Point (EP) bonus for this race.
     * @return The EP bonus.
     */
    public int getEpBonus() {
        return this.raceBonus.getEpBonus();
    }
    
    /**
     * Convenience method to get the extra ability slot bonus for this race.
     * @return The extra ability slot bonus.
     */
    public int getExtraAbilitySlots() {
        return this.raceBonus.getExtraAbilitySlots();
    }

    /**
     * Returns the enum name (e.g., "ELF") for display or persistence.
     */
    @Override
    public String toString() {
        return name();
    }

    /**
     * Converts all enum values into a string array for UI dropdowns.
     *
     * @return array of race names
     */
    public static String[] valuesAsStringArray() {
        return Arrays.stream(values())
                     .map(Enum::name)
                     .toArray(String[]::new);
    }
}