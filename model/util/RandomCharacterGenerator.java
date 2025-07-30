package model.util;

import model.core.Ability;
import model.core.Character;
import model.core.ClassType;
import model.core.RaceType;
import model.service.ClassService;
import model.service.RaceService;
import model.util.Constants;

import java.util.List;
import java.util.Random;
import model.util.GameException;

/**
 * Utility for generating simple random characters used for AI opponents.
 */
public final class RandomCharacterGenerator {
    private RandomCharacterGenerator() {}

    /**
     * Generates a randomly constructed {@link Character} for use as an AI-controlled opponent.
     * <p>
     * The character is assigned a random {@link RaceType}, a random {@link ClassType}, and
     * a random set of abilities based on the chosen class and race's bonus ability slots.
     *
     * @param name the name to assign to the generated character
     * @return a fully initialized {@code Character} with randomized attributes and abilities
     * @throws GameException if ability generation fails due to service-level constraints
     */
    public static Character generate(String name) throws GameException {
        Random rng = new Random();
        RaceService raceService = RaceService.INSTANCE;
        ClassService classService = ClassService.INSTANCE;

        List<RaceType> races = raceService.getAvailableRaces();
        RaceType race = races.get(rng.nextInt(races.size()));

        ClassType[] classes = ClassType.values();
        ClassType clazz = classes[rng.nextInt(classes.length)];

        Character c = new Character(name, race, clazz);
        int abilityCount = Constants.NUM_ABILITIES_PER_CHAR + race.getExtraAbilitySlots();
        List<Ability> abilities = classService.getRandomAbilitiesForClass(clazz, abilityCount);
        c.setAbilities(abilities);
        return c;
    }
}
