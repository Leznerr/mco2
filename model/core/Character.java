package model.core;

import model.battle.LevelingSystem;
import model.item.Inventory;
import model.item.MagicItem;
import model.util.Constants;
import model.util.GameException;
import model.util.InputValidator;
import model.util.StatusEffect;
import model.util.StatusEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a combat-ready party member in Fatal Fantasy: Tactics.
 * <p>
 * This is the core domain model, storing all character state including stats,
 * inventory, abilities, and status effects. It is responsible for enforcing
 * the game's mechanical constraints on its own state.
 */
public class Character {

    // --- Core Immutable Attributes ---
    private final String name;
    private final RaceType race;
    private final ClassType classType;

    // --- Core Mutable Attributes ---
    private final List<Ability> abilities;
    private final Inventory inventory;
    private final List<StatusEffect> activeStatusEffects;
    private MagicItem equippedItem;

    // --- Dynamic Stats ---
    private int maxHp;
    private int currentHp;
    private int maxEp;
    private int currentEp;
    private int hp;
    private int ep;
  

    

    // --- Progression ---
    private int level;
    private int xp;
    private int winCount;

    // --- Temporary Battle State ---
    private boolean isStunned;

    /**
     * The designated constructor for creating a new, fully-validated Character.
     * All other constructors must chain to this one.
     *
     * @param name      The non-blank name for the character.
     * @param race      The non-null RaceType of the character.
     * @param classType The non-null ClassType of the character.
     * @param abilities A non-null list of abilities (must contain 0 or 3 items).
     * @throws GameException if any validation pre-conditions fail.
     */
    public Character(String name, RaceType race, ClassType classType, List<Ability> abilities) throws GameException {
        // --- Pre-condition Validation ---
        InputValidator.requireNonBlank(name, "Character name");
        InputValidator.requireNonNull(race, "Race");
        InputValidator.requireNonNull(classType, "Class");
        InputValidator.requireNonNull(abilities, "Initial abilities list");
        if (!abilities.isEmpty() && abilities.size() != Constants.NUM_ABILITIES_PER_CHAR) {
            throw new GameException("A character must start with exactly " + Constants.NUM_ABILITIES_PER_CHAR + " abilities, or none.");
        }

        // --- Initialization ---
        this.name = name;
        this.race = race;
        this.classType = classType;
        this.abilities = new ArrayList<>(abilities); // Defensive copy
        this.inventory = new Inventory();
        this.activeStatusEffects = new ArrayList<>();
        this.equippedItem = null;
        this.isStunned = false;

        initializeStats();
    }

    /**
     * Convenience constructor for creating a character with no initial abilities.
     * Chains to the designated constructor.
     */
    public Character(String name, RaceType race, ClassType classType) throws GameException {
        this(name, race, classType, new ArrayList<>());
    }

    /**
     * Initializes all stats based on class and race, and resets progression.
     */
    private void initializeStats() {
        // Start with class base stats, then apply race bonuses.
        this.maxHp = this.classType.getBaseHP() + this.race.getHpBonus();
        this.maxEp = this.classType.getBaseEP() + this.race.getEpBonus();
        
        // Set current stats to max
        this.currentHp = this.maxHp;
        this.currentEp = this.maxEp;

        // Initialize progression
        this.level = 1;
        this.xp = 0;
        this.winCount = 0;
    }

    // --- Getters for Core Attributes ---

    public String getName() { return name; }
    public RaceType getRaceType() { return race; }
    public ClassType getClassType() { return classType; }
    public List<Ability> getAbilities() { return Collections.unmodifiableList(abilities); }
    public Inventory getInventory() { return inventory; }
    public MagicItem getEquippedItem() { return equippedItem; }

    // --- Getters for Dynamic Stats ---

    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public int getMaxEp() { return maxEp; }
    public int getCurrentEp() { return currentEp; }
    public boolean isAlive() { return currentHp > 0; }

    // --- Getters for Progression ---

    public int getLevel() { return level; }
    public int getXp() { return xp; }
    public int getWinCount() { return winCount; }

    // --- Combat State Management ---

    /**
     * Applies a specified amount of damage, ensuring HP does not fall below 0.
     * @param damage The non-negative amount of damage to apply.
     */
    public void takeDamage(int damage) {
        if (damage < 0) return; // Or throw exception for invalid input
        this.currentHp = Math.max(0, this.currentHp - damage);
    }

    /**
     * Restores a specified amount of HP, ensuring it does not exceed max HP.
     * @param healingAmount The non-negative amount of HP to restore.
     */
    public void heal(int healingAmount) {
        if (healingAmount < 0) return;
        this.currentHp = Math.min(this.maxHp, this.currentHp + healingAmount);
    }

    /**
     * Attempts to spend a specified amount of EP.
     * @param cost The non-negative EP cost.
     * @return {@code true} if EP was spent, {@code false} if insufficient EP.
     */
    public boolean spendEp(int cost) {
        if (cost < 0 || this.currentEp < cost) {
            return false;
        }
        this.currentEp -= cost;
        return true;
    }

    /**
     * Restores a specified amount of EP, ensuring it does not exceed max EP.
     * @param amount The non-negative amount of EP to restore.
     */
    public void gainEp(int amount) {
        if (amount < 0) return;
        this.currentEp = Math.min(this.maxEp, this.currentEp + amount);
    }

    // --- Progression Management ---

    /**
     * Adds experience points and checks for a level-up.
     * Note: Delegates the level-up logic to the LevelingSystem service.
     * @param amount The non-negative amount of XP to add.
     */
    public void addXp(int amount) {
        if (amount < 0) return;
        this.xp += amount;
        LevelingSystem.processLevelUp(this); // External service handles the logic
    }
    
    // Internal state modification for progression; should only be called by trusted services like LevelingSystem.
    public void setLevel(int level) { this.level = level; }
    public void setMaxStats(int newMaxHp, int newMaxEp) {
        this.maxHp = newMaxHp;
        this.maxEp = newMaxEp;
        // Optionally restore to full health/energy on level up
        this.currentHp = newMaxHp;
        this.currentEp = newMaxEp;
    }

    public void recordWin() { this.winCount++; }

    // --- Equipment & Ability Management ---

    public void equipItem(MagicItem item) {
        InputValidator.requireNonNull(item, "Item to equip");
        this.equippedItem = item;
    }

    public void unequipItem() {
        this.equippedItem = null;
    }

    /**
     * Replaces the character's current abilities with a new set.
     * @param newAbilities A list containing exactly the required number of abilities.
     */
    public void setAbilities(List<Ability> newAbilities) {
        InputValidator.requireNonNull(newAbilities, "New abilities list");
        InputValidator.requireSize(newAbilities, Constants.NUM_ABILITIES_PER_CHAR,
            "A character must have exactly " + Constants.NUM_ABILITIES_PER_CHAR + " abilities.");
        this.abilities.clear();
        this.abilities.addAll(newAbilities);
    }

    // --- Status Effect Management ---

    public boolean hasStatusEffect(StatusEffectType type) {
        return activeStatusEffects.stream().anyMatch(effect -> effect.getType() == type);
    }

    public void addStatusEffect(StatusEffect effect) {
        InputValidator.requireNonNull(effect, "Status effect");
        if (activeStatusEffects.size() >= Constants.MAX_STATUS_EFFECTS) {
            return; // Or throw exception
        }
        activeStatusEffects.add(effect);
        effect.applyEffect(this);
    }

    public void removeStatusEffect(StatusEffectType type) {
        activeStatusEffects.removeIf(effect -> effect.getType() == type);
    }

    public List<StatusEffect> getActiveStatusEffects() {
        return Collections.unmodifiableList(activeStatusEffects);
    }
    
    public boolean isStunned() { return this.isStunned; }
    public void setStunned(boolean stunned) { this.isStunned = stunned; }

    // --- Overridden Methods ---

    @Override
    public String toString() {
        return String.format(
            "%s (Lvl %d %s %s, HP: %d/%d, EP: %d/%d)",
            name, level, race.name(), classType.name(),
            currentHp, maxHp, currentEp, maxEp
        );
    }

     public int getHp() {
        return hp;
    }


    public int getEp() {
        return ep;
    }



    public String getAbilitiesDescription() {
        StringBuilder descriptions = new StringBuilder();
        for (Ability ability : abilities) {
            descriptions.append(ability.getDescription()).append("\n"); // Assuming Ability has a getDescription method
        }
        return descriptions.toString();
    }
}