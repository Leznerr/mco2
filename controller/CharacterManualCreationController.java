package controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.core.Ability;
import model.core.Character;
import model.core.ClassType;
import model.core.Player;
import model.core.RaceType;
import model.service.ClassService;
import model.service.RaceService;
import model.util.GameException;
import model.util.InputValidator;
import view.CharacterManualCreationView;

/**
 * Controller for Manual Character Creation screen in Fatal Fantasy: Tactics.
 * <ul>
 *   <li>Binds all buttons and combo-boxes in {@link CharacterManualCreationView}.</li>
 *   <li>Validates user input (name, race, class, and 3 distinct abilities).</li>
 *   <li>Creates a {@link Character} and attaches it to the correct {@link Player}.</li>
 * </ul>
 * <p>
 *  Strictly separated from view, production-ready, robust to user and data errors,
 *  and unit-testable.
 * </p>
 */
public final class CharacterManualCreationController {

    // --- Fields ---
    private final CharacterManualCreationView view;
    private final String playerName;
    private final GameManagerController gameManagerController;
    private final RaceService raceService;
    private final ClassService classService;

    // --- Constructor ---
    /**
     * Construct the controller and bind events.
     *
     * @param view                  Manual Character Creation view (non-null)
     * @param playerName            The name of the player (non-null)
     * @param gameManagerController Main game manager/controller (non-null)
     * @throws GameException if any argument is null
     */
    public CharacterManualCreationController(
            CharacterManualCreationView view,
            String playerName,
            GameManagerController gameManagerController
    ) throws GameException {
        InputValidator.requireNonNull(view, "view");
        InputValidator.requireNonNull(playerName, "playerName");
        InputValidator.requireNonNull(gameManagerController, "gameManagerController");
        this.view = view;
        this.playerName = playerName;
        this.gameManagerController = gameManagerController;
        this.raceService = RaceService.INSTANCE;
        this.classService = ClassService.INSTANCE;

        initViewData();
        bindUI();
    }

    // --- View Data Initialization ---

    /**
     * Initializes the view with race and class options.
     * <p>
     * Ability dropdowns are initially cleared until a class is selected.
     * </p>
     */
    private void initViewData() {
        // Populate races and classes; abilities will be refreshed on class selection
        view.setRaceOptions(
                raceService.getAvailableRaces()
                        .stream().map(Enum::name).toArray(String[]::new)
        );
        view.setClassOptions(
                Arrays.stream(ClassType.values())
                        .map(Enum::name).toArray(String[]::new)
        );
        clearAbilityOptions();
    }

    /**
     * Clears all ability dropdowns in the view.
     */
    private void clearAbilityOptions() {
        view.setAbilityOptions(1, new String[0]);
        view.setAbilityOptions(2, new String[0]);
        view.setAbilityOptions(3, new String[0]);
        view.setAbilityOptions(4, new String[0]);
    }

    // --- UI Event Binding ---

    /**
     * Binds all UI event listeners (button clicks and combo box selections)
     * to their respective handler methods.
     */
    private void bindUI() {
        view.addCreateCharacterListener(e -> handleCreateCharacter());
        view.addReturnListener(e -> handleReturn());
        view.addClassDropdownListener(e -> handleClassSelection());
        view.addRaceDropdownListener(e -> handleRaceSelection());
    }

    /**
     * Handles the "Return" button event.
     * <p>
     * Disposes the current view and navigates back to the player's character
     * management screen.
     * </p>
     */
    private void handleReturn() {
        view.dispose();
        Player player = getPlayerByName(this.playerName);
        gameManagerController.handleNavigateToCharacterManagement(player);
    }

    // --- Event Handlers ---

    /**
     * Handles the creation of a new character based on user input.
     * <ul>
     *   <li>Validates input (name, race, class, and number/uniqueness of abilities).</li>
     *   <li>Ensures selected abilities match class restrictions.</li>
     *   <li>Creates a new character and adds it to the appropriate player.</li>
     *   <li>Persists the game state and closes the view on success.</li>
     * </ul>
     * <p>
     * Shows error messages on validation or business rule failure.
     * </p>
     */
    private void handleCreateCharacter() {
        try {
            String name = view.getCharacterName().trim();
            String raceStr = view.getSelectedRace();
            String classStr = view.getSelectedClass();
            String[] selectedAbilityNames = view.getSelectedAbilities();

            // Input validation
            InputValidator.requireNonBlank(name, "Character name");
            InputValidator.requireNonNull(raceStr, "Race");
            InputValidator.requireNonNull(classStr, "Class");

            RaceType race = RaceType.valueOf(raceStr);

            int expectedAbilities = 3 + (race == RaceType.GNOME ? 1 : 0);

            long count = Arrays.stream(selectedAbilityNames)
                    .filter(a -> a != null && !a.isBlank()).count();
            InputValidator.requireSize((int) count, expectedAbilities, expectedAbilities,
                    "You must choose exactly " + expectedAbilities + " abilities.");

            // Validate abilities are unique
            InputValidator.requireDistinct(
                    Arrays.stream(selectedAbilityNames)
                            .filter(a -> a != null && !a.isBlank())
                            .collect(Collectors.toList()),
                    "All selected abilities must be unique.");

            // Convert to enums/objects
            ClassType classType = ClassType.valueOf(classStr);
            String[] usedNames = Arrays.stream(selectedAbilityNames)
                    .filter(a -> a != null && !a.isBlank())
                    .toArray(String[]::new);
            List<Ability> abilities = classService.getAbilitiesByNames(usedNames);

            // Business rule: first three abilities must match class
            List<String> allowedAbilityNames = classService.getAvailableAbilities(classType)
                    .stream().map(Ability::getName).collect(Collectors.toList());
            for (int i = 0; i < 3; i++) {
                String abilityName = selectedAbilityNames[i];
                if (!allowedAbilityNames.contains(abilityName)) {
                    throw new GameException("Ability \"" + abilityName + "\" is not valid for the selected class.");
                }
            }

            // Attach character to player
            Character newCharacter = new Character(name, race, classType);
            newCharacter.setAbilities(abilities);

            Player player = getPlayerByName(this.playerName);
            player.addCharacter(newCharacter);

            // Persist the updated game data
            gameManagerController.handleSaveGameRequest();

            view.showInfoMessage("Character \"" + name + "\" successfully created!");
            view.dispose();

        } catch (GameException ge) {
            view.showErrorMessage(ge.getMessage());
        } catch (IllegalArgumentException iae) {
            view.showErrorMessage("Invalid race, class, or ability selected.");
        }
    }

    /**
     * Handles changes to the selected class.
     * <p>
     * Refreshes the ability dropdown options based on the selected class.
     * </p>
     */
    private void handleClassSelection() {
        refreshAbilityOptions();
    }

    /**
     * Handles changes to the selected race.
     * <p>
     * Shows or hides the fourth ability dropdown if the selected race is GNOME.
     * Also refreshes the ability dropdown options.
     * </p>
     */
    private void handleRaceSelection() {
        String raceStr = view.getSelectedRace();
        if (raceStr != null && !raceStr.isBlank()) {
            RaceType race = RaceType.valueOf(raceStr);
            view.setAbility4Visible(race == RaceType.GNOME);
        } else {
            view.setAbility4Visible(false);
        }
        refreshAbilityOptions();
    }

    /**
     * Refreshes ability dropdown options based on the selected class and race.
     * <p>
     * Populates the first three slots with class-specific abilities and the
     * fourth slot if the race is GNOME.
     * </p>
     */
    private void refreshAbilityOptions() {
        clearAbilityOptions();

        String classStr = view.getSelectedClass();

        if (classStr != null && !classStr.isBlank()) {
            try {
                ClassType classType = ClassType.valueOf(classStr);
                List<String> abilities = classService.getAvailableAbilities(classType)
                        .stream()
                        .map(Ability::getName)
                        .collect(Collectors.toList());

                String[] opts = abilities.toArray(new String[0]);
                for (int i = 1; i <= 3; i++) {
                    view.setAbilityOptions(i, opts);
                }

                // If race allows a fourth ability slot (gnome), populate it as well
                String raceStr = view.getSelectedRace();
                if (raceStr != null && !raceStr.isBlank() && RaceType.valueOf(raceStr) == RaceType.GNOME) {
                    view.setAbilityOptions(4, opts);
                }
            } catch (GameException e) {
                // In case of an error fetching abilities, keep the dropdowns empty
                clearAbilityOptions();
            }
        }
    }

    // --- Helper: Find Player by Name ---

    /**
     * Finds a player by their name (case-insensitive).
     *
     * @param playerName The name of the player to find
     * @return The matching {@link Player} instance
     * @throws IllegalArgumentException if no matching player is found
     */
    private Player getPlayerByName(String playerName) {
        return gameManagerController.getPlayers().stream()
                .filter(p -> p.getName().equalsIgnoreCase(playerName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerName));
    }

}
