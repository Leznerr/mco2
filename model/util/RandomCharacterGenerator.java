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
