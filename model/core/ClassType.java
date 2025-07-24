package model.core;

import java.util.Arrays;

/**
 * {@code ClassType} enumerates the archetypal job classes available to a
 * {@link Character} in <strong>Fatal Fantasy: Tactics</strong>.
 *
 * <p>Each constant provides a description for use in UI elements like the
 * {@code CharacterCreationView}.</p>
 *
 * <h3>Specification Traceability</h3>
 * <ul>
 *   <li><strong>MCO2 – Model Modification:</strong> Character class must be updated to include its Class</li>
 * </ul>
 */
public enum ClassType {

    /** Masters of arcane damage and support spells. */
    MAGE("Glass-cannon spell-caster with powerful elemental attacks.", 100, 50),

    /** Agile combatants excelling in critical strikes and evasion. */
    ROGUE("High agility skirmisher that relies on speed and stealth.", 100, 50),

    /** Heavily armoured front-liner with strong physical defence. */
    WARRIOR("Resilient melee fighter boasting superior HP and defence.", 100, 50);

    private final String description;
    private final int baseHP;
    private final int baseEP;

    /**
     * Constructs a ClassType with description and base stats.
     *
     * @param description a user-facing class description
     * @param baseHP      default HP value for this class
     * @param baseEP      default EP value for this class
     */
    ClassType(String description, int baseHP, int baseEP) {
        this.description = description;
        this.baseHP = baseHP;
        this.baseEP = baseEP;
    }

    /**
     * Gets the description of the class type.
     *
     * @return the user-friendly description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the base HP of the class.
     *
     * @return base HP value
     */
    public int getBaseHP() {
        return baseHP;
    }

    /**
     * Returns the base EP of the class.
     *
     * @return base EP value
     */
    public int getBaseEP() {
        return baseEP;
    }

    @Override
    public String toString() {
        return name();
    }

    /**
     * Returns an array of enum names as strings for selection UIs.
     *
     * @return a string array of enum names
     */
    public static String[] valuesAsStringArray() {
        return Arrays.stream(values())
                     .map(Enum::name)
                     .toArray(String[]::new);
    }
}
