package model.util;

import model.core.Ability;
import model.core.AbilityEffectType; // Corrected import
import model.util.StatusEffectType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry of all defined abilities in the game. This class provides a 
 * centralized, immutable source for all abilities to avoid duplication and 
 * ensure consistency.
 */
public final class AbilityRegistry {

    private static final Map<String, Ability> abilities;

    // This block runs once when the class is first loaded into memory.
    static {
        try {
            Map<String, Ability> tempAbilities = new HashMap<>();

            // --- Mage Abilities ---
            tempAbilities.put("Arcane Bolt", new Ability(
                "Arcane Bolt",
                "Launch a basic magical projectile that deals 20 arcane damage to the target.",
                Constants.ARCANE_BOLT_COST,
                AbilityEffectType.DAMAGE,
                Constants.ARCANE_BOLT_DMG,
                StatusEffectType.NONE
            ));
            tempAbilities.put("Arcane Blast", new Ability(
                "Arcane Blast",
                "Unleash a burst of fiery energy, dealing 65 arcane damage to the target.",
                Constants.ARCANE_BLAST_COST,
                AbilityEffectType.DAMAGE,
                Constants.ARCANE_BLAST_DMG,
                StatusEffectType.NONE
            ));
            tempAbilities.put("Mana Channel", new Ability(
                "Mana Channel",
                "Draw upon ambient magical energy to restore your own. Restores 15 EP.",
                Constants.MANA_CHANNEL_COST,
                AbilityEffectType.ENERGY_GAIN,
                Constants.MANA_CHANNEL_GAIN,
                StatusEffectType.NONE
            ));
            tempAbilities.put("Lesser Heal", new Ability(
                "Lesser Heal",
                "Weave a minor healing spell to mend your wounds. Restores 40 HP.",
                Constants.LESSER_HEAL_COST,
                AbilityEffectType.HEAL,
                Constants.LESSER_HEAL_HP,
                StatusEffectType.NONE
            ));
            tempAbilities.put("Arcane Shield", new Ability(
                "Arcane Shield",
                "Conjure a protective barrier. You take no damage for the round.",
                Constants.ARCANE_SHIELD_COST,
                AbilityEffectType.DEFENSE,
                0, // Effect value not applicable for full immunity
                StatusEffectType.IMMUNITY
            ));

            // --- Rogue Abilities ---
            tempAbilities.put("Shiv", new Ability(
                "Shiv", "A quick, precise stab that deals 20 physical damage.",
                Constants.SHIV_COST, AbilityEffectType.DAMAGE, Constants.SHIV_DMG, StatusEffectType.NONE
            ));
            tempAbilities.put("Backstab", new Ability(
                "Backstab", "Strike a vital point and deal 35 points of physical damage.",
                Constants.BACKSTAB_COST, AbilityEffectType.DAMAGE, Constants.BACKSTAB_DMG, StatusEffectType.NONE
            ));
            tempAbilities.put("Focus", new Ability(
                "Focus", "Take a moment to concentrate, restoring 10 EP.",
                Constants.FOCUS_COST, AbilityEffectType.ENERGY_GAIN, Constants.FOCUS_GAIN, StatusEffectType.NONE
            ));
            tempAbilities.put("Smoke Bomb", new Ability(
                "Smoke Bomb", "You have a 50% chance of evading any incoming attacks in the current round.",
                Constants.SMOKE_BOMB_COST, AbilityEffectType.EVADE, 0, StatusEffectType.EVADING
            ));
            tempAbilities.put("Sneak Attack", new Ability(
                "Sneak Attack", "Evade all attacks while dealing 45 physical damage.",
                Constants.SNEAK_ATTACK_COST, AbilityEffectType.DAMAGE, Constants.SNEAK_ATTACK_DMG, StatusEffectType.IMMUNITY
            ));

            // --- Warrior Abilities ---
            tempAbilities.put("Cleave", new Ability(
                "Cleave", "A sweeping strike that deals 20 physical damage.",
                Constants.CLEAVE_COST, AbilityEffectType.DAMAGE, Constants.CLEAVE_DMG, StatusEffectType.NONE
            ));
            tempAbilities.put("Shield Bash", new Ability(
                "Shield Bash", "Slam your shield into the opponent, dealing 35 physical damage.",
                Constants.SHIELD_BASH_COST, AbilityEffectType.DAMAGE, Constants.SHIELD_BASH_DMG, StatusEffectType.NONE
            ));
            tempAbilities.put("Ironclad Defense", new Ability(
                "Ironclad Defense", "Brace yourself, effectively taking no damage for the current round.",
                Constants.IRONCLAD_DEFENSE_COST, AbilityEffectType.DEFENSE, 0, StatusEffectType.IMMUNITY
            ));
            tempAbilities.put("Bloodlust", new Ability(
                "Bloodlust", "Tap into your inner fury, restoring 30 HP.",
                Constants.BLOODLUST_COST, AbilityEffectType.HEAL, Constants.BLOODLUST_HP, StatusEffectType.NONE
            ));
            tempAbilities.put("Rallying Cry", new Ability(
                "Rallying Cry", "Let out a powerful shout, recovering 12 EP.",
                Constants.RALLYING_CRY_COST, AbilityEffectType.ENERGY_GAIN, Constants.RALLYING_CRY_GAIN, StatusEffectType.NONE
            ));

            // Make the map unmodifiable and assign it
            abilities = Collections.unmodifiableMap(tempAbilities);

        } catch (GameException e) {
            // If any ability fails to initialize, it's a critical configuration error.
            throw new IllegalStateException("FATAL: Failed to initialize AbilityRegistry", e);
        }
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private AbilityRegistry() {
        throw new UnsupportedOperationException("Utility class – instantiation not allowed");
    }

    /**
     * Retrieves an Ability from the registry by its unique name.
     * @param name The case-sensitive name of the ability.
     * @return The corresponding Ability object, or null if not found.
     */
    public static Ability getAbilityByName(String name) {
        return abilities.get(name);
    }

    /**
     * Returns a safe, unmodifiable view of all abilities in the game.
     * @return An unmodifiable Map of all registered abilities.
     */
    public static Map<String, Ability> getAllAbilities() {
        return abilities;
    }
}