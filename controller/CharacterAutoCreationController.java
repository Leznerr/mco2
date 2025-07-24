package controller;

import model.core.Ability;
import model.core.Character;
import model.core.ClassType;
import model.core.Player;
import model.core.RaceType;
import model.service.ClassService;
import model.service.RaceService;
import model.util.GameException;
import model.util.InputValidator;
import view.CharacterAutoCreationView;

import java.util.List;
import java.util.Random;

/**
 * Controller for the automatic character creation GUI screen in Fatal Fantasy: Tactics (MCO2).
 * <p>
 * Handles random character generation, preview, confirmation, and all related UI events.
 * Strictly follows MVC: no view rendering or model persistence inside this class.
 * </p>
 */
public final class CharacterAutoCreationController {

    /** Predefined pool of names used for random character generation. */
    private static final List<String> NAME_POOL = List.of(
            "Aragon", "Legolas", "Gimli", "Frodo", "Gandalf", "Eowyn", "Boromir", "Elrond"
    );

    private final CharacterAutoCreationView view;
    private final String playerName;
    private final GameManagerController gameManagerController;
    private final ClassService classService = ClassService.INSTANCE;
    private final RaceService raceService = RaceService.INSTANCE;
    private final Random random = new Random();

    /**
     * Constructs the auto character creation controller.
     *
     * @param view                  the associated view (non-null)
     * @param playerName            the name of the player creating a character (non-null)
     * @param gameManagerController the main game manager controller (non-null)
     * @throws GameException if parameters are null
     */
    public CharacterAutoCreationController(
            CharacterAutoCreationView view,
            String playerName,
            GameManagerController gameManagerController
    ) throws GameException {
        InputValidator.requireNonNull(view, "CharacterAutoCreationView");
        InputValidator.requireNonNull(playerName, "playerName");
        InputValidator.requireNonNull(gameManagerController, "GameManagerController");

        this.view = view;
        this.playerName = playerName;
        this.gameManagerController = gameManagerController;

        bindUI();
    }

    /**
     * Binds all UI actions to their corresponding handler methods.
     */
    private void bindUI() {
        view.addActionListener(e -> {
            String command = e.getActionCommand();
            switch (command) {
                case CharacterAutoCreationView.CREATE -> handleAutoCreateCharacter();
                case CharacterAutoCreationView.RETURN -> handleReturn();
                case CharacterAutoCreationView.RANDOMIZE -> handleRandomize();
                default -> view.showErrorMessage("Unknown command: " + command);
            }
        });
    }

    /**
     * Handles the creation of a character with randomly generated values.
     * Performs all validation, updates the player model, and closes the dialog.
     */
    private void handleAutoCreateCharacter() {
        try {
            String name = generateRandomName();
            RaceType race = getRandomRace();
            ClassType classType = getRandomClass();
            List<Ability> abilities = classService.getRandomAbilitiesForClass(classType, 3);

            if (view.confirmCharacterCreation(name)) {
                Player player = getPlayerByName(playerName);

                Character newCharacter = new Character(name, race, classType);
                newCharacter.setAbilities(abilities);

                player.addCharacter(newCharacter);
                view.showInfoMessage("Character \"" + name + "\" created successfully!");
                view.dispose();
            }
        } catch (GameException ge) {
            view.showErrorMessage("Failed to create character: " + ge.getMessage());
        }
    }

    /**
     * Randomizes and previews a character's details (not persisted until user confirms).
     */
    private void handleRandomize() {
        String name = generateRandomName();
        RaceType race = getRandomRace();
        ClassType classType = getRandomClass();
        List<Ability> abilities = classService.getRandomAbilitiesForClass(classType, 3);

        StringBuilder details = new StringBuilder();
        details.append("Name: ").append(name).append("\n")
                .append("Race: ").append(race.name()).append("\n")
                .append("Class: ").append(classType.name()).append("\n")
                .append("Abilities: ");
        for (Ability a : abilities) {
            details.append(a.getName()).append(", ");
        }
        if (!abilities.isEmpty()) {
            details.setLength(details.length() - 2); // remove trailing comma
        }
        view.showGeneratedDetails(details.toString());
    }

    /**
     * Navigates back to the main menu or previous screen via the GameManagerController.
     */
    private void handleReturn() {
        view.dispose();
        gameManagerController.navigateBackToMainMenu();
    }

    /**
     * Selects a random name from the predefined pool.
     *
     * @return a random character name
     */
    private String generateRandomName() {
        return NAME_POOL.get(random.nextInt(NAME_POOL.size()));
    }

    /**
     * Randomly selects a race from those currently available in the RaceService.
     *
     * @return a random RaceType
     * @throws GameException if no races are available
     */
    private RaceType getRandomRace() {
        List<RaceType> availableRaces = raceService.getAvailableRaces().stream().toList();
        if (availableRaces.isEmpty()) {
            throw new IllegalStateException("No available races for character generation.");
        }
        return availableRaces.get(random.nextInt(availableRaces.size()));
    }

    /**
     * Randomly selects a class from all defined class types.
     *
     * @return a random ClassType
     */
    private ClassType getRandomClass() {
        ClassType[] classes = ClassType.values();
        return classes[random.nextInt(classes.length)];
    }

    /**
     * Retrieves the player instance by name.
     *
     * @param playerName the player name (case-insensitive)
     * @return the Player with matching name
     * @throws IllegalArgumentException if no player is found with the provided name
     */
    private Player getPlayerByName(String playerName) {
        return gameManagerController.getPlayers().stream()
                .filter(p -> p.getName().equalsIgnoreCase(playerName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerName));
    }
}
