package model.core;

import model.util.Constants;
import model.util.GameException;
import model.util.InputValidator;
import model.util.StatusEffectType;

/**
 * Represents a combat ability (attack, heal, or status effect).
 * Immutable and metadata-driven. Holds no executable logic.
 */
public final class Ability {

    private final String name;
    private final String description;
    private final int epCost;
    private final AbilityEffectType abilityEffectType;
    private final int effectValue;
    private final StatusEffectType statusEffectApplied;
    private int cost;

    /**
     * Full constructor for abilities with full metadata.
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
        return cost;
    }


    public StatusEffectType getStatusEffectApplied() {
        return statusEffectApplied;
    }

    // --- Equality & String ---

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof Ability other && name.equals(other.name));
    }

    @Override
    public String toString() {
        return name;
    }


}

