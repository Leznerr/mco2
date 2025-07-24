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
import model.util.GameException;
import model.util.InputValidator;

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
        mage.add(new Ability("Arcane Bolt", "Deal 20 damage.", 5, AbilityEffectType.DAMAGE, 20, null));
        mage.add(new Ability("Mana Surge", "Gain 10 EP.", 0, AbilityEffectType.ENERGY_GAIN, 10, null));
        abilities.put(ClassType.MAGE, Collections.unmodifiableList(mage));

        List<Ability> rogue = new ArrayList<>();
        rogue.add(new Ability("Backstab", "Deal 15 damage ignoring defence.", 4, AbilityEffectType.DAMAGE, 15, null));
        rogue.add(new Ability("Shadow Veil", "Increase evasion for 2 turns.", 3, AbilityEffectType.UTILITY, 0, null));
        abilities.put(ClassType.ROGUE, Collections.unmodifiableList(rogue));

        List<Ability> warrior = new ArrayList<>();
        warrior.add(new Ability("Power Strike", "Deal 25 damage.", 6, AbilityEffectType.DAMAGE, 25, null));
        warrior.add(new Ability("Fortify", "Raise defence for 2 turns.", 4, AbilityEffectType.UTILITY, 0, null));
        abilities.put(ClassType.WARRIOR, Collections.unmodifiableList(warrior));

        CLASS_ABILITIES = Collections.unmodifiableMap(abilities);

        EnumMap<ClassType, String> desc = new EnumMap<>(ClassType.class);
        desc.put(ClassType.MAGE, "Scholars of the arcane who rain elemental fury.");
        desc.put(ClassType.ROGUE, "Shadow-dancing skirmishers with deadly precision.");
        desc.put(ClassType.WARRIOR, "Front-line fighters whose steel and grit hold fast.");
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
            boolean found = false;
            for (Ability ability : allAbilities) {
                if (ability.getName().equalsIgnoreCase(name)) {
                    matched.add(ability);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new GameException("Ability not found: " + name);
            }
        }

        return matched;
    }

    /**
     * Internal helper to aggregate all abilities from every class.
     *
     * @return immutable list of all defined {@link Ability} instances
     */
    private List<Ability> getAllAbilities() {
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
        List<Ability> all = getAvailableAbilities(classType);
        Collections.shuffle(all); // Randomize the order
        return all.stream().limit(count).collect(Collectors.toList());
    }
}
