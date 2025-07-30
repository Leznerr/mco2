package model.core;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.battle.CombatLog;
import model.battle.LevelingSystem;
import model.item.Inventory;
import model.item.MagicItem;
import model.item.SingleUseItem;
import model.util.Constants;
import model.util.GameException;
import model.util.InputValidator;
import model.util.StatusEffect;
import model.util.StatusEffectType;

/**
 * Represents a combat-ready party member in Fatal Fantasy: Tactics.
 * <p>
 * This is the core domain model, storing all character state including stats,
 * inventory, abilities, and status effects. It is responsible for enforcing
 * the game's mechanical constraints on its own state.
 */
public class Character implements Serializable {

    private static final long serialVersionUID = 1L;

    // --- Core Immutable Attributes ---
    private final String name;
    private final RaceType race;
    private final ClassType classType;

    // --- Core Mutable Attributes ---
    /** Character abilities. Always initialized. Never null. */
    private final List<Ability> abilities = new ArrayList<>();
    /** Inventory for magic items. Always initialized. Never null. */
    private final Inventory inventory;
    /** Status effects currently affecting the character. Always initialized. Never null. */
    private transient List<StatusEffect> activeStatusEffects = new ArrayList<>();

    // --- Dynamic Stats ---
    private int maxHp;
    private int currentHp;
    private int maxEp;
    private int currentEp;
  

    

    // --- Progression ---
    private int level;
    private int experience;
    private int battlesWon;
    private int nextLevelMilestone;
    private int abilitySlots;
    private int unlockedAbilitySlots;
    /** Tracks how many ability slots are currently available. */
    private int abilitySlotCount;

    // --- Temporary Battle State ---
    private boolean isStunned;
    private boolean statusEffectImmunityUsed;
    private boolean phoenixReviveUsed;
    private boolean vitalityBonusApplied;

    /**
     * Creates a deep copy of the given character for use in battle.
     * 
     * @param other The character to copy. Must not be null.
     * @throws GameException if the source character is invalid.
     */
    public Character(Character other) throws GameException {
        InputValidator.requireNonNull(other, "character to copy");

        this.name = other.name;
        this.race = other.race;
        this.classType = other.classType;

        this.abilities.clear();
        this.abilities.addAll(other.abilities);

        this.inventory = new Inventory(other.inventory);

        this.activeStatusEffects = new ArrayList<>();

        this.maxHp = other.maxHp;
        this.currentHp = other.currentHp;
        this.maxEp = other.maxEp;
        this.currentEp = other.currentEp;

        this.level = other.level;
        this.experience = other.experience;
        this.battlesWon = other.battlesWon;
        this.nextLevelMilestone = other.nextLevelMilestone;
        this.abilitySlots = other.abilitySlots;
        this.unlockedAbilitySlots = other.unlockedAbilitySlots;
        this.abilitySlotCount = other.abilitySlotCount;

        this.isStunned = false;
        this.statusEffectImmunityUsed = false;
        this.phoenixReviveUsed = false;
        this.vitalityBonusApplied = false;
    }

    /**
     * Creates a new battle-ready copy of this character.
     * 
     * @return A deep-copied {@code Character} for battle.
     * @throws GameException if copying fails due to invalid data.
     */
    public Character copyForBattle() throws GameException {
        return new Character(this);
    }

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
        int expected = Constants.NUM_ABILITIES_PER_CHAR + race.getExtraAbilitySlots();
        if (!abilities.isEmpty() && abilities.size() != expected) {
            throw new GameException("A character must start with exactly " + expected + " abilities, or none.");
        }

        // --- Initialization ---
        this.name = name;
        this.race = race;
        this.classType = classType;

        this.inventory = new Inventory();

        this.abilities.clear();
        this.abilities.addAll(abilities); // Defensive copy

        this.activeStatusEffects.clear();
        this.isStunned = false;
        this.vitalityBonusApplied = false;

        initializeStats();
        if (!this.abilities.isEmpty()) {
            this.unlockedAbilitySlots = Math.min(this.abilities.size(), this.abilitySlots);
        }
        this.abilitySlotCount = this.unlockedAbilitySlots;
    }

    /**
     * Convenience constructor for creating a character with no initial abilities.
     * Chains to the designated constructor.
     *
     * @param name      The character's name.
     * @param race      The race type.
     * @param classType The class type.
     * @throws GameException if character creation fails.
     */
    public Character(String name, RaceType race, ClassType classType) throws GameException {
        this(name, race, classType, new ArrayList<>());
    }

    /**
     * Initializes stats (HP, EP, progression) based on class and race bonuses.
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
        this.experience = 0;
        this.battlesWon = 0;
        this.nextLevelMilestone = 5;
        this.abilitySlots = Constants.NUM_ABILITIES_PER_CHAR + race.getExtraAbilitySlots();
        this.unlockedAbilitySlots = this.abilitySlots;
        this.abilitySlotCount = this.abilitySlots;
    }

    // --- Getters for Core Attributes ---

    public String getName() { return name; }
    public RaceType getRaceType() { return race; }
    public ClassType getClassType() { return classType; }
    public List<Ability> getAbilities() { return Collections.unmodifiableList(abilities); }
    public Inventory getInventory() { return inventory; }

    /**
     * Shortcut to retrieve the item currently equipped by this character.
     *
     * @return the equipped {@link MagicItem}, or {@code null} if none
     */
    public MagicItem getEquippedItem() {
        return inventory.getEquippedItem();
    }

    // --- Getters for Dynamic Stats ---

    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public int getMaxEp() { return maxEp; }
    public int getCurrentEp() { return currentEp; }
    public boolean isAlive() { return currentHp > 0; }

    // --- Getters for Progression ---

    public int getLevel() { return level; }
    public int getXp() { return experience; }
    public int getExperience() { return experience; }
    public int getBattlesWon() { return battlesWon; }
    public int getNextLevelMilestone() { return nextLevelMilestone; }
    public int getAbilitySlots() { return abilitySlots; }
    public int getUnlockedAbilitySlots() { return unlockedAbilitySlots; }
    public int getAbilitySlotCount() { return abilitySlotCount; }

    // --- Combat State Management ---

    /**
     * Applies a specified amount of damage, modified by status effects.
     * HP will not drop below zero.
     * 
     * @param damage The non-negative amount of damage to apply.
     */
    public void takeDamage(int damage) {
        if (damage >= 0) {

            int finalDamage = damage;

        if (hasStatusEffect(StatusEffectType.IMMUNITY)) {
            finalDamage = 0;
        } else {
            if (hasStatusEffect(StatusEffectType.DEFENSE_UP)) {
                finalDamage = (int) Math.ceil(finalDamage / 2.0);
            }

            if (hasStatusEffect(StatusEffectType.SHIELDED)) {
                StatusEffect chosen = null;
                for (StatusEffect se : activeStatusEffects) {
                    if (chosen == null && se.getType() == StatusEffectType.SHIELDED && se instanceof model.util.effects.ShieldEffect) {
                        chosen = se;
                    }
                }
                if (chosen instanceof model.util.effects.ShieldEffect s) {
                    finalDamage = s.absorb(finalDamage);
                }
                removeStatusEffect(StatusEffectType.SHIELDED);
            }

            if (hasStatusEffect(StatusEffectType.EVADING) && new java.util.Random().nextBoolean()) {
                finalDamage = 0;
            }
        }

        if (hasStatusEffect(StatusEffectType.MARKED)) {
            finalDamage += 5;
        }

            this.currentHp = Math.max(0, this.currentHp - finalDamage);
        }
    }

    /**
     * Checks if the Phoenix Feather is equipped and revives the character if eligible.
     * 
     * @param log The combat log to add messages to.
     * @return {@code true} if the character was revived by Phoenix Feather.
     * @throws GameException if inventory access fails.
     */
    public boolean checkPhoenixFeather(CombatLog log) throws GameException {
        if (!isAlive()
                && !phoenixReviveUsed
                && inventory.getEquippedItem() instanceof model.item.PassiveItem p
                && "Phoenix Feather".equals(p.getName())) {
            this.currentHp = Math.min(40, this.maxHp);
            phoenixReviveUsed = true;
            inventory.removeItem(p);
            if (log != null) {
                log.addEntry(name + " is revived by Phoenix Feather!");
            }
            return true;
        }
        return false;
    }

    /**
     * Heals the character by the given amount, up to max HP.
     *
     * @param healingAmount The amount to heal.
     */
    public void heal(int healingAmount) {
        if (healingAmount >= 0) {
            this.currentHp = Math.min(this.maxHp, this.currentHp + healingAmount);
        }
    }

    /**
     * Increases both current and max HP by a specified amount.
     *
     * @param amount The HP bonus to apply.
     */
    public void increaseMaxHp(int amount) {
        if (amount > 0) {
            this.maxHp += amount;
            this.currentHp += amount;
        }
    }

    /**
     * Attempts to spend EP from the character.
     *
     * @param cost The EP cost to pay.
     * @return {@code true} if sufficient EP was available and deducted; {@code false} otherwise.
     */
    public boolean spendEp(int cost) {
        if (cost < 0 || this.currentEp < cost) {
            return false;
        }
        this.currentEp -= cost;
        return true;
    }

    /**
     * Restores EP up to the maximum allowed.
     *
     * @param amount Amount of EP to restore.
     */
    public void gainEp(int amount) {
        if (amount >= 0) {
            this.currentEp = Math.min(this.maxEp, this.currentEp + amount);
        }
    }

    // --- Progression Management ---

    /**
     * Adds experience points to this character.
     *
     * @param amount The XP to add.
     */
    public void addExperience(int amount) {
        if (amount >= 0) {
            this.experience += amount;
        }
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

    /**
     * Increments battle won
     */
    public void incrementBattlesWon() { this.battlesWon++; }

    /** 
     * Checks if can level up
     */
    public boolean canLevelUp() { return battlesWon >= nextLevelMilestone; }

    /**
     * Levels up the character if enough battles have been won.
     * Increases stats and may unlock new ability slots.
     */
    public void levelUp() {
        if (canLevelUp()) {
            int previousLevel = this.level;
            this.level++;
            this.battlesWon -= nextLevelMilestone;
            this.nextLevelMilestone += 5;

            LevelingSystem.processLevelUp(this);

            unlockAbilitySlot(previousLevel, this.level);
        }
    }

    /**
     * Unlocks ability slots for milestone levels (e.g., 2, 4).
     *
     * @param oldLevel The level before leveling up.
     * @param newLevel The level after leveling up.
     */
    private void unlockAbilitySlot(int oldLevel, int newLevel) {
        for (int lvl = oldLevel + 1; lvl <= newLevel; lvl++) {
            if (lvl == 2 || lvl == 4) {
                this.abilitySlots++;
                this.unlockedAbilitySlots = Math.min(this.unlockedAbilitySlots + 1, this.abilitySlots);
                this.abilitySlotCount = this.unlockedAbilitySlots;
            }
        }
    }

    // --- Equipment & Ability Management ---

    /**
     * Equips the given item from this character's inventory.
     *
     * @param item the item to equip (non-null and must be in inventory)
     * @throws GameException if validation fails
     */
    public void equipItem(MagicItem item) throws GameException {
        inventory.equipItem(item);
    }

    /**
     * Unequips any currently equipped item.
     */
    public void unequipItem() {
        inventory.unequipItem();
    }

    /**
     * Checks whether this character currently possesses the given item.
     *
     * @param item the item to check (non-null)
     * @return {@code true} if present
     */
    public boolean hasItem(MagicItem item) {
        return inventory.hasItem(item);
    }

    /**
     * Consumes a single-use item from the inventory.
     *
     * @param item the item to use (non-null and owned by this character)
     * @throws GameException if the item is invalid or not owned
     */
    public void useSingleUseItem(SingleUseItem item) throws GameException {
        inventory.useSingleUseItem(item);
    }

    /**
     * Replaces the character's current abilities with a new set.
     * 
     * @param newAbilities A list containing exactly the required number of abilities.
     */
    public void setAbilities(List<Ability> newAbilities) {
        InputValidator.requireNonNull(newAbilities, "New abilities list");
        if (newAbilities.size() > abilitySlotCount) {
            throw new IllegalArgumentException("A character can equip at most " + abilitySlotCount + " abilities.");
        }
        this.abilities.clear();
        this.abilities.addAll(newAbilities);
    }

    // --- Status Effect Management ---

    public boolean hasStatusEffect(StatusEffectType type) {
        return activeStatusEffects.stream().anyMatch(effect -> effect.getType() == type);
    }

    /**
     * Adds a new status effect to this character.
     * May be blocked by equipped items or effect limits.
     *
     * @param effect The status effect to apply.
     */
    public void addStatusEffect(StatusEffect effect) {
        InputValidator.requireNonNull(effect, "Status effect");
        if (inventory.getEquippedItem() instanceof model.item.PassiveItem p
                && "Elven Cloak".equals(p.getName())
                && !statusEffectImmunityUsed) {
            statusEffectImmunityUsed = true;
        } else if (activeStatusEffects.size() >= Constants.MAX_STATUS_EFFECTS) {
            // Too many status effects - optionally throw exception
        } else {
            activeStatusEffects.add(effect);
            effect.applyEffect(this);
        }
    }

    /**
     * Removes the given type of status effect from the character.
     *
     * @param type The status effect type to remove.
     */
    public void removeStatusEffect(StatusEffectType type) {
        activeStatusEffects.removeIf(effect -> effect.getType() == type);
    }

    public List<StatusEffect> getActiveStatusEffects() {
        return Collections.unmodifiableList(activeStatusEffects);
    }

    public boolean isStunned() { return this.isStunned; }
    public void setStunned(boolean stunned) { this.isStunned = stunned; }

    public boolean isVitalityBonusApplied() { return vitalityBonusApplied; }
    public void setVitalityBonusApplied(boolean applied) { this.vitalityBonusApplied = applied; }

    /**
     * Processes all status effects at the start of the character's turn.
     * Triggers poison damage and removes expired effects.
     *
     * @param log The combat log for output.
     * @throws GameException if effect logic fails.
     */
    public void processStartOfTurnEffects(CombatLog log) throws GameException {
        InputValidator.requireNonNull(log, "combat log");
        var iterator = activeStatusEffects.iterator();
        while (iterator.hasNext()) {
            StatusEffect effect = iterator.next();
            int beforeHp = this.currentHp;
            effect.onTurnStart(this);
            if (effect.getType() == StatusEffectType.POISONED) {
                int dmg = beforeHp - this.currentHp;
                if (dmg > 0) {
                    log.addEntry(this.name + " suffers " + dmg + " poison damage.");
                }
            }
            if (effect.getDuration() <= 0) {
                effect.remove(this);
                iterator.remove();
                log.addEntry(this.name + " is no longer " + effect.getType() + ".");
            }
        }
    }

    /**
     * Processes all status effects at the end of the character's turn.
     * Removes effects whose duration has expired.
     *
     * @param log The combat log for output.
     * @throws GameException if effect logic fails.
     */
    public void processEndOfTurnEffects(CombatLog log) throws GameException {
        InputValidator.requireNonNull(log, "combat log");
        var iterator = activeStatusEffects.iterator();
        while (iterator.hasNext()) {
            StatusEffect effect = iterator.next();
            effect.onTurnEnd(this);
            if (effect.getDuration() <= 0) {
                effect.remove(this);
                iterator.remove();
                log.addEntry(this.name + " is no longer " + effect.getType() + ".");
            }
        }
    }

    // --- Overridden Methods ---

    /**
     * Returns a formatted string representation of the character, including their
     * name, level, race, class, HP, EP, XP, number of wins, and XP milestone for the next level.
     *
     * @return A string describing the character’s current state.
     */
    @Override
    public String toString() {
        return String.format(
            "%s (Lvl %d %s %s, HP: %d/%d, EP: %d/%d, XP: %d, Wins: %d/%d)",
            name, level, race.name(), classType.name(),
            currentHp, maxHp, currentEp, maxEp,
            experience, battlesWon, nextLevelMilestone
        );
    }

    public String getAbilitiesDescription() {
        StringBuilder descriptions = new StringBuilder();
        for (Ability ability : abilities) {
            descriptions.append(ability.getDescription()).append("\n"); // Assuming Ability has a getDescription method
        }
        return descriptions.toString();
    }

    /**
     * Restores transient state during deserialization.
     *
     * @param in The input stream used for reading.
     * @throws IOException if reading fails.
     * @throws ClassNotFoundException if a class in the stream is not found.
     */
    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (activeStatusEffects == null) {
            activeStatusEffects = new ArrayList<>();
        }
        if (nextLevelMilestone == 0) {
            nextLevelMilestone = 5;
        }
        if (abilitySlots == 0) {
            abilitySlots = Constants.NUM_ABILITIES_PER_CHAR + race.getExtraAbilitySlots();
        }
        if (unlockedAbilitySlots == 0) {
            unlockedAbilitySlots = abilitySlots;
        }
        if (abilitySlotCount == 0) {
            abilitySlotCount = unlockedAbilitySlots;
        }
        // ensure booleans are initialised
        statusEffectImmunityUsed = false;
        phoenixReviveUsed = false;
        vitalityBonusApplied = false;
    }
}