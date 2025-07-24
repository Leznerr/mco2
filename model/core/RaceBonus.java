package model.core;

import model.util.GameException;
import model.util.InputValidator;

/**
 * Immutable Data-Transfer Object encapsulating the stat bonuses that a
 * {@link RaceType} grants during character creation.
 *
 * <h2>Fields</h2>
 * <ul>
 *   <li>{@code hpBonus} – additional maximum hit-points</li>
 *   <li>{@code epBonus} – additional maximum energy points</li>
 *   <li>{@code extraAbilitySlots} – extra ability-slot capacity</li>
 * </ul>
 *
 * <p>Instances are <strong>immutable</strong>; validation occurs in the
 * constructor via {@link InputValidator}. This supports the rubric’s
 * <em>Reusability</em> and <em>Maintainability</em> goals.</p>
 *
 * <h3>Specification Traceability</h3>
 * <ul>
 *   <li><strong>MCO2 – Race System:</strong> Each race must provide stat bonuses on creation.</li>
 * </ul>
 *
 * @author Group 17
 * @see RaceType
 */
public final class RaceBonus {

    /** Additional maximum HP granted by the race. */
    private final int hpBonus;

    /** Additional maximum EP granted by the race. */
    private final int epBonus;

    /** Extra ability-slot capacity granted by the race. */
    private final int extraAbilitySlots;

    /**
     * Creates a new immutable bonus set.
     *
     * @param hpBonus           positive or zero HP bonus
     * @param epBonus           positive or zero EP bonus
     * @param extraAbilitySlots positive or zero ability-slot bonus
     * @throws GameException if any argument is negative
     */
    public RaceBonus(int hpBonus, int epBonus, int extraAbilitySlots)
            throws GameException {

        InputValidator.requirePositiveOrZero(hpBonus, "HP bonus");
        InputValidator.requirePositiveOrZero(epBonus, "EP bonus");
        InputValidator.requirePositiveOrZero(extraAbilitySlots, "Ability-slot bonus");

        this.hpBonus = hpBonus;
        this.epBonus = epBonus;
        this.extraAbilitySlots = extraAbilitySlots;
    }

    /**
     * @return the HP bonus (≥ 0)
     */
    public int getHpBonus() {
        return hpBonus;
    }

    /**
     * @return the EP bonus (≥ 0)
     */
    public int getEpBonus() {
        return epBonus;
    }

    /**
     * @return the extra ability-slot bonus (≥ 0)
     */
    public int getExtraAbilitySlots() {
        return extraAbilitySlots;
    }

    /**
     * Returns a human-readable summary for debugging and logging.
     *
     * @return string representation of the bonus attributes
     */
    @Override
    public String toString() {
        return "RaceBonus{hp=" + hpBonus +
               ", ep=" + epBonus +
               ", slots=" + extraAbilitySlots + '}';
    }
}
