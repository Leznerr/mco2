package model.core;

import model.util.Constants;
import model.util.GameException;
import model.util.InputValidator;
import model.util.StatusEffectType;
import java.io.Serializable;

/**
 * Represents a combat ability (attack, heal, or status effect).
 * Immutable and metadata-driven. Holds no executable logic.
 */
public final class Ability implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;
    private final int epCost;
    private final AbilityEffectType abilityEffectType;
    private final int effectValue;
    private final StatusEffectType statusEffectApplied;

    /**
     * Constructs a new {@code Ability} with the specified metadata.
     *
     * @param name                the name of the ability (must be non-blank)
     * @param description         the description of the ability (must be non-blank)
     * @param epCost              the energy point cost to use the ability (must be in [0, MAX_EP_COST])
     * @param effectType          the type of effect this ability applies (must not be {@code null})
     * @param effectValue         the magnitude of the effect (must be in [0, MAX_EFFECT_VALUE])
     * @param statusEffectApplied the status effect applied, if any (required if {@code effectType} is {@code APPLY_STATUS})
     * @throws GameException if any argument fails validation
     */
    public Ability(String name, String description, int epCost, AbilityEffectType effectType,
                   int effectValue, StatusEffectType statusEffectApplied) throws GameException {

        InputValidator.requireNonBlank(name, "Ability name");
        InputValidator.requireNonBlank(description, "Ability description");
        InputValidator.requireRange(epCost, 0, Constants.MAX_EP_COST, "EP cost");
        InputValidator.requireNonNull(effectType, "Ability effect type");
        InputValidator.requireRange(effectValue, 0, Constants.MAX_EFFECT_VALUE, "Effect value");

        if (effectType == AbilityEffectType.APPLY_STATUS) {
            InputValidator.requireNonNull(statusEffectApplied, "StatusEffect (APPLY_STATUS)");
        }

        this.name = name;
        this.description = description;
        this.epCost = epCost;
        this.abilityEffectType = effectType;
        this.effectValue = effectValue;
        this.statusEffectApplied = statusEffectApplied;
    }



    // --- Getters ---

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getEpCost() {
        return epCost;
    }

    public AbilityEffectType getAbilityEffectType() {
        return abilityEffectType;
    }

    public int getEffectValue() {
        return effectValue;
    }


    public int getCost() {
        return epCost;
    }


    public StatusEffectType getStatusEffectApplied() {
        return statusEffectApplied;
    }

    // --- Equality & String ---

    /**
     * Computes the hash code for this ability based on its name.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Checks whether this ability is equal to another object.
     * Two abilities are considered equal if they have the same name.
     *
     * @param obj the object to compare
     * @return {@code true} if the object is an {@code Ability} with the same name, otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof Ability other && name.equals(other.name));
    }

    /**
     * Returns the string representation of this ability, which is its name.
     *
     * @return the name of the ability
     */
    @Override
    public String toString() {
        return name;
    }


}

