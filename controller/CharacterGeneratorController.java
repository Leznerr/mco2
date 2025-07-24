package controller;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import model.core.Ability;
import model.core.ClassType;
import model.core.RaceType;
import model.core.Character; // Explicit to avoid clash with java.lang.Character
import model.service.ClassService;
import model.service.RaceService;
import model.util.GameException;
import model.util.InputValidator;
import view.CharacterAutoCreationView;

/**
 * Controller for the <strong>Auto Character Creation</strong> screen.
 *
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Receives all button events from {@link CharacterAutoCreationView}.</li>
 *   <li>Uses {@link RaceService} and {@link ClassService} to generate a
 *       fully-random {@link Character} with <em>three unique</em> abilities.</li>
 *   <li>Updates the preview area in the view.</li>
 *   <li>Persists the final character via the supplied {@code saveCallback}.</li>
 * </ul>
 * </p>
 * <p>
 * All UI interaction is encapsulated by the view. This controller is framework-agnostic
 * and fully unit-testable.
 * </p>
 */
public final class CharacterGeneratorController {

    // --- Dependencies (injected via constructor) ---
    private final CharacterAutoCreationView view;
    private final RaceService raceService;
    private final ClassService classService;
    private final Random rng;
    private final Consumer<Character> saveCallback; // Typically: player::addCharacter

    // --- Constructor ---
    /**
     * Constructs the auto character creation controller.
     *
     * @param view         The GUI view (must be non-null)
     * @param raceService  Service for available races (must be non-null, non-empty)
     * @param classService Service for available classes (must be non-null)
     * @param rng          Random number generator (optional, will default to new Random() if null)
     * @param saveCallback Function to persist the created character (must be non-null)
     * @throws GameException if any dependency is null, or no races available
     */
    public CharacterGeneratorController(
            CharacterAutoCreationView view,
            RaceService raceService,
            ClassService classService,
            Random rng,
            Consumer<Character> saveCallback
    ) throws GameException {
        // Validate dependencies
        InputValidator.requireNonNull(view, "view");
        InputValidator.requireNonNull(raceService, "raceService");
        InputValidator.requireNonNull(classService, "classService");
        InputValidator.requireNonNull(saveCallback, "saveCallback");
        if (raceService.getAvailableRaces().isEmpty()) {
            throw new GameException("RaceService has no races loaded.");
        }
        this.view = view;
        this.raceService = raceService;
        this.classService = classService;
        this.saveCallback = saveCallback;
        this.rng = (rng != null) ? rng : new Random();
        wireView();
    }

    // --- Event wiring ---
    private void wireView() {
        view.addActionListener(e -> {
            String cmd = e.getActionCommand();
            switch (cmd) {
                case CharacterAutoCreationView.RANDOMIZE -> onRandomize();
                case CharacterAutoCreationView.CREATE    -> onCreate();
                case CharacterAutoCreationView.RETURN    -> view.dispose();
                default -> { /* no-op, future-proof */ }
            }
        });
    }

    // --- Event Handlers ---
    /** Generates a preview character and displays its details in the view. */
    private void onRandomize() {
        try {
            Character preview = generateRandomCharacter();
            view.showGeneratedDetails(format(preview));
            // Name field is left untouched for manual override
        } catch (GameException ge) {
            view.showErrorMessage("Unable to generate character:\n" + ge.getMessage());
        }
    }

    /** Attempts to persist the character using the user-specified name. */
    private void onCreate() {
        try {
            String chosenName = view.getCharacterName().trim();
            if (chosenName.isEmpty()) {
                view.showErrorMessage("Please enter a character name first.");
                return;
            }
            boolean confirmed = view.confirmCharacterCreation(chosenName);
            if (confirmed) {
                Character finalChar = generateRandomCharacterWithName(chosenName);
                saveCallback.accept(finalChar);
                view.showInfoMessage("Character \"" + chosenName + "\" created!");
                view.resetFields();
            }
        } catch (GameException ge) {
            view.showErrorMessage("Creation failed: " + ge.getMessage());
        } catch (Exception ex) {
            view.showErrorMessage("Unexpected error: " + ex.getMessage());
        }
    }

    // --- Core Random Character Generation ---
    /** Generates a fully random character (including a random name). */
    private Character generateRandomCharacter() throws GameException {
        String randomName = "Hero" + (1000 + rng.nextInt(9000));
        return generateRandomCharacterWithName(randomName);
    }

    /** Generates a random character but uses the supplied name. */
    private Character generateRandomCharacterWithName(String name) throws GameException {
        List<RaceType> races = raceService.getAvailableRaces();
        RaceType race = races.get(rng.nextInt(races.size()));

        ClassType[] classTypes = ClassType.values();
        ClassType classType = classTypes[rng.nextInt(classTypes.length)];

        List<Ability> pool = classService.getAvailableAbilities(classType);
        if (pool.size() < 3) {
            throw new GameException("Class \"" + classType + "\" has fewer than 3 abilities configured.");
        }

        // Shuffle and pick first 3 unique abilities
        Collections.shuffle(pool, rng);
        List<Ability> chosen = pool.subList(0, 3);
        if (!areDistinct(chosen)) {
            throw new GameException("Duplicate abilities detected for class " + classType);
        }

        Character character = new Character(name, race, classType);
        character.setAbilities(chosen);
        return character;
    }

    // --- Utility Helpers ---
    /** Checks that all ability names are unique (defensive, in case of config issues). */
    private static boolean areDistinct(List<Ability> abilities) {
        return abilities.stream().map(Ability::getName).distinct().count() == abilities.size();
    }

    /** Formats a character for preview display. */
    private static String format(Character c) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Name : ").append(c.getName()).append('\n');
        sb.append("Race : ").append(c.getRaceType()).append('\n');
        sb.append("Class: ").append(c.getClassType()).append('\n');
        sb.append("Abilities:\n");
        for (Ability a : c.getAbilities()) {
            sb.append("  • ").append(a.getName()).append('\n');
        }
        return sb.toString();
    }
}
