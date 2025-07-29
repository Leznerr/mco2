package model.service;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

import model.core.Ability;
import model.core.ClassType;
import model.core.AbilityEffectType;
import model.util.StatusEffectType;
import model.util.GameException;
import model.util.InputValidator;
import model.util.AbilityRegistry;

/**
 * Provides access to predefined {@link Ability} and descriptions per {@link ClassType}
 * in <em>Fatal Fantasy: Tactics</em>.
 *
 * <p><strong>Design Principles:</strong></p>
 * <ul>
 *   <li><strong>Singleton:</strong> Accessed via {@link #INSTANCE}</li>
 *   <li><strong>Immutability:</strong> All collections wrapped in {@code Collections.unmodifiableXxx}</li>
 *   <li><strong>Defensive Programming:</strong> Validates all parameters</li>
 *   <li><strong>No UI Logic:</strong> Strictly model/service only</li>
 * </ul>
 */
public final class ClassService {

    private static final Map<ClassType, List<Ability>> CLASS_ABILITIES;
    private static final Map<ClassType, String> CLASS_DESCRIPTIONS;

    static {
        EnumMap<ClassType, List<Ability>> abilities = new EnumMap<>(ClassType.class);

        List<Ability> mage = new ArrayList<>();
        mage.add(AbilityRegistry.getAbilityByName("Arcane Bolt"));
        mage.add(AbilityRegistry.getAbilityByName("Arcane Blast"));
        mage.add(AbilityRegistry.getAbilityByName("Mana Channel"));
        mage.add(AbilityRegistry.getAbilityByName("Lesser Heal"));
        mage.add(AbilityRegistry.getAbilityByName("Arcane Shield"));
        abilities.put(ClassType.MAGE, Collections.unmodifiableList(mage));

        List<Ability> rogue = new ArrayList<>();
        rogue.add(AbilityRegistry.getAbilityByName("Shiv"));
        rogue.add(AbilityRegistry.getAbilityByName("Backstab"));
        rogue.add(AbilityRegistry.getAbilityByName("Focus"));
        rogue.add(AbilityRegistry.getAbilityByName("Smoke Bomb"));
        rogue.add(AbilityRegistry.getAbilityByName("Sneak Attack"));
        abilities.put(ClassType.ROGUE, Collections.unmodifiableList(rogue));

        List<Ability> warrior = new ArrayList<>();
        warrior.add(AbilityRegistry.getAbilityByName("Cleave"));
        warrior.add(AbilityRegistry.getAbilityByName("Shield Bash"));
        warrior.add(AbilityRegistry.getAbilityByName("Ironclad Defense"));
        warrior.add(AbilityRegistry.getAbilityByName("Bloodlust"));
        warrior.add(AbilityRegistry.getAbilityByName("Rallying Cry"));
        abilities.put(ClassType.WARRIOR, Collections.unmodifiableList(warrior));

        List<Ability> paladin = new ArrayList<>();
        paladin.add(new Ability("Smite", "Deal 30 holy damage.", 7, AbilityEffectType.DAMAGE, 30, null));
        paladin.add(new Ability("Divine Shield", "Become immune to damage this turn.", 8, AbilityEffectType.DEFENSE, 0, null));
        paladin.add(new Ability("Holy Light", "Heal for 20 HP.", 6, AbilityEffectType.HEAL, 20, null));
        paladin.add(new Ability("Righteous Fury", "Strike for 25 damage.", 6, AbilityEffectType.DAMAGE, 25, null));
        paladin.add(new Ability("Guardian's Blessing", "Restore 10 EP.", 0, AbilityEffectType.ENERGY_GAIN, 10, null));
        abilities.put(ClassType.PALADIN, Collections.unmodifiableList(paladin));

        List<Ability> summoner = new ArrayList<>();
        summoner.add(new Ability("Summon Spirit Wolf", "Deal 22 damage and drain 2 EP.", 7, AbilityEffectType.DAMAGE, 22, null));
        summoner.add(new Ability("Elemental Pact", "Deal 28 damage and heal 12 HP.", 10, AbilityEffectType.DAMAGE, 28, null));
        summoner.add(new Ability("Protective Wisp", "Gain a shield absorbing damage.", 8, AbilityEffectType.DEFENSE, 0, StatusEffectType.SHIELDED));
        summoner.add(new Ability("Arcane Binding", "50% chance to stun the enemy.", 6, AbilityEffectType.APPLY_STATUS, 50, StatusEffectType.STUNNED));
        summoner.add(new Ability("Greater Summoning", "Deal 38 damage and gain 8 EP.", 14, AbilityEffectType.DAMAGE, 38, null));
        abilities.put(ClassType.SUMMONER, Collections.unmodifiableList(summoner));

        List<Ability> engineer = new ArrayList<>();
        engineer.add(new Ability("Deploy Turret", "Deal 20 damage and mark the target.", 9, AbilityEffectType.DAMAGE, 20, StatusEffectType.MARKED));
        engineer.add(new Ability("Repair Drone", "Heal self for 25 HP.", 7, AbilityEffectType.HEAL, 25, null));
        engineer.add(new Ability("EMP Grenade", "Deal 18 damage and drain 6 EP.", 8, AbilityEffectType.DAMAGE, 18, null));
        engineer.add(new Ability("Defensive Matrix", "Gain immunity for 1 turn.", 11, AbilityEffectType.DEFENSE, 0, StatusEffectType.IMMUNITY));
        engineer.add(new Ability("Overclock", "Deal 32 damage but lose 6 HP.", 13, AbilityEffectType.DAMAGE, 32, null));
        abilities.put(ClassType.ENGINEER, Collections.unmodifiableList(engineer));

        CLASS_ABILITIES = Collections.unmodifiableMap(abilities);

        EnumMap<ClassType, String> desc = new EnumMap<>(ClassType.class);
        desc.put(ClassType.MAGE, "Scholars of the arcane who rain elemental fury.");
        desc.put(ClassType.ROGUE, "Shadow-dancing skirmishers with deadly precision.");
        desc.put(ClassType.WARRIOR, "Front-line fighters whose steel and grit hold fast.");
        desc.put(ClassType.PALADIN, "Holy warriors combining defence with divine magic.");
        desc.put(ClassType.SUMMONER, "Mystics who conjure spirits to aid them.");
        desc.put(ClassType.ENGINEER, "Inventors wielding gadgets and explosives.");
        CLASS_DESCRIPTIONS = Collections.unmodifiableMap(desc);
    }

    /** Singleton instance. */
    public static final ClassService INSTANCE = new ClassService();

    /** Private constructor to prevent external instantiation. */
    private ClassService() {}

    /**
     * Returns an immutable list of starter {@link Ability} objects for a given class.
     *
     * @param classType non-null character class
     * @return unmodifiable list of {@code Ability} objects
     * @throws GameException if {@code classType} is null or unregistered
     */
    public List<Ability> getAvailableAbilities(ClassType classType) throws GameException {
        InputValidator.requireNonNull(classType, "classType");
        List<Ability> list = CLASS_ABILITIES.get(classType);
        if (list == null) {
            throw new GameException("No ability list registered for " + classType);
        }
        return list;
    }

    /**
     * Gets the flavor text description for a specific {@link ClassType}.
     *
     * @param classType non-null class type
     * @return non-blank description string
     * @throws GameException if {@code classType} is null or not registered
     */
    public String getClassDescription(ClassType classType) throws GameException {
        InputValidator.requireNonNull(classType, "classType");
        String desc = CLASS_DESCRIPTIONS.get(classType);
        if (desc == null) {
            throw new GameException("No description registered for " + classType);
        }
        return desc;
    }

    /**
     * Resolves a list of {@link Ability} objects from their string names.
     * Used during character creation or data loading.
     *
     * @param names non-null array of ability names
     * @return list of {@code Ability} objects matching names
     * @throws GameException if any name is not found
     */
    public List<Ability> getAbilitiesByNames(String[] names) throws GameException {
        InputValidator.requireNonNull(names, "Ability names");

        List<Ability> allAbilities = getAllAbilities();
        List<Ability> matched = new ArrayList<>();

        for (String name : names) {
            Ability found = null;
            int i = 0;
            while (i < allAbilities.size() && found == null) {
                Ability ability = allAbilities.get(i);
                if (ability.getName().equalsIgnoreCase(name)) {
                    found = ability;
                }
                i++;
            }
            if (found == null) {
                throw new GameException("Ability not found: " + name);
            }
            matched.add(found);
        }

        return matched;
    }

    /**
     * Internal helper to aggregate all abilities from every class.
     *
     * @return immutable list of all defined {@link Ability} instances
     */
    public List<Ability> getAllAbilities() {
        List<Ability> all = new ArrayList<>();
        for (List<Ability> abilityList : CLASS_ABILITIES.values()) {
            all.addAll(abilityList);
        }
        return Collections.unmodifiableList(all);
    }
      /**
     * Returns a randomized subset of abilities for the given class.
     * @param classType The class to get abilities for
     * @param count Number of random abilities to return
     * @return List of random Ability instances
     */
    public List<Ability> getRandomAbilitiesForClass(ClassType classType, int count) {
        List<Ability> all = new ArrayList<>(getAvailableAbilities(classType));
        Collections.shuffle(all); // Randomize the order
        return all.stream().limit(count).collect(Collectors.toList());
    }
}
