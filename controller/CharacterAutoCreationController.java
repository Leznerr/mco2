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

    // Stores the last generated character components so the user can confirm
    private RaceType generatedRace;
    private ClassType generatedClass;
    private List<Ability> generatedAbilities;

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
     * Binds UI button actions to their corresponding handler methods.
     * <p>
     * Handles create, randomize, and return commands from the view.
     * </p>
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
     * Handles character creation based on previously randomized values.
     * <p>
     * Validates the input name, checks if generation occurred, constructs a new character,
     * saves it to the player profile, and updates the UI accordingly.
     * </p>
     */
    private void handleAutoCreateCharacter() {
        String name = view.getCharacterName();
        if (name.isBlank()) {
            view.showErrorMessage("Please enter a character name.");
        } else if (generatedRace == null || generatedClass == null || generatedAbilities == null) {
            view.showErrorMessage("Please randomize a character first.");
        } else {
            try {
                if (view.confirmCharacterCreation(name)) {
                    Player player = getPlayerByName(playerName);
                    Character newCharacter = new Character(name, generatedRace, generatedClass, generatedAbilities);
                    player.addCharacter(newCharacter);
                    gameManagerController.handleSaveGameRequest();

                    view.showInfoMessage("Character \"" + name + "\" created successfully!");
                    view.dispose();
                    gameManagerController.handleNavigateToCharacterManagement(player);
                }
            } catch (GameException ge) {
                view.showErrorMessage("Failed to create character: " + ge.getMessage());
            }
        }
    }

    /**
     * Generates a new character with random attributes for preview.
     * <p>
     * Selects a random name, race, class, and ability list. Gnomes receive an additional ability.
     * Updates the view with the generated preview.
     * </p>
     */
    private void handleRandomize() {
        String name = generateRandomName();
        generatedRace = getRandomRace();
        generatedClass = getRandomClass();
        generatedAbilities = classService.getRandomAbilitiesForClass(generatedClass, 3);
        if (generatedRace == RaceType.GNOME) {
            List<Ability> all = classService.getAllAbilities();
            generatedAbilities.add(all.get(random.nextInt(all.size())));
        }

        try {
            Character preview = new Character(name, generatedRace, generatedClass, generatedAbilities);
            String details = formatCharacter(preview);
            view.setCharacterName(name);
            view.showGeneratedDetails(details);
        } catch (GameException ge) {
            view.showErrorMessage("Error generating character: " + ge.getMessage());
        }
    }

    /**
     * Handles the return action by disposing of the current view and navigating back to
     * the character management screen via the game manager controller.
     */
    private void handleReturn() {
        view.dispose();
        Player player = getPlayerByName(this.playerName);
        gameManagerController.handleNavigateToCharacterManagement(player);
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
     * Retrieves the player object with the given name, ignoring case.
     *
     * @param playerName the name of the player to retrieve
     * @return the matching Player object
     * @throws IllegalArgumentException if the player does not exist
     */
    private Player getPlayerByName(String playerName) {
        return gameManagerController.getPlayers().stream()
                .filter(p -> p.getName().equalsIgnoreCase(playerName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerName));
    }

    /**
     * Formats a character's attributes and abilities into a readable string for preview display.
     *
     * @param c the character to format
     * @return formatted string with name, race, class, HP/EP stats, and abilities
     */
    private static String formatCharacter(Character c) {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(c.getName()).append('\n');
        sb.append("Race: ").append(c.getRaceType()).append(" (HP+")
          .append(c.getRaceType().getHpBonus()).append(", EP+")
          .append(c.getRaceType().getEpBonus()).append(")\n");
        sb.append("Class: ").append(c.getClassType()).append('\n');
        sb.append("Max HP: ").append(c.getMaxHp()).append('\n');
        sb.append("Max EP: ").append(c.getMaxEp()).append('\n');
        sb.append("Abilities:\n");
        for (Ability a : c.getAbilities()) {
            sb.append("  - ").append(a.getName()).append('\n');
        }
        return sb.toString();
    }
}
