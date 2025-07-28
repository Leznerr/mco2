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
import model.util.Constants;
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

    private void clearAbilityOptions() {
        int slots = view.getAbilityCount();
        for (int i = 1; i <= slots; i++) {
            view.setAbilityOptions(i, new String[0]);
        }
    }

    // --- UI Event Binding ---
    private void bindUI() {
        view.addCreateCharacterListener(e -> handleCreateCharacter());
        view.addReturnListener(e -> handleReturn());
        view.addClassDropdownListener(e -> handleClassSelection());
        view.addRaceDropdownListener(e -> handleRaceSelection());
    }

    private void handleReturn() {
        view.dispose();
        Player player = getPlayerByName(this.playerName);
        gameManagerController.handleNavigateToCharacterManagement(player);
    }

    // --- Event Handlers ---
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

            int expectedAbilities = Constants.NUM_ABILITIES_PER_CHAR + race.getExtraAbilitySlots();

            long count = Arrays.stream(selectedAbilityNames)
                    .filter(a -> a != null && !a.isBlank()).count();
            InputValidator.requireSize((int) count, expectedAbilities, expectedAbilities,
                    "You must choose exactly " + expectedAbilities + " abilities.");

            // Validate abilities are unique
            if (!areDistinct(selectedAbilityNames)) {
                throw new GameException("All selected abilities must be unique.");
            }

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

    private void handleClassSelection() {
        refreshAbilityOptions();
    }

    private void handleRaceSelection() {
        String raceStr = view.getSelectedRace();
        if (raceStr != null && !raceStr.isBlank()) {
            RaceType race = RaceType.valueOf(raceStr);
            int count = Constants.NUM_ABILITIES_PER_CHAR + race.getExtraAbilitySlots();
            view.setAbilityCount(count);
        } else {
            view.setAbilityCount(Constants.NUM_ABILITIES_PER_CHAR);
        }
        refreshAbilityOptions();
    }

    private void refreshAbilityOptions() {
        String classStr = view.getSelectedClass();
        if (classStr == null || classStr.isBlank()) {
            clearAbilityOptions();
            return;
        }
        try {
            ClassType classType = ClassType.valueOf(classStr);
            List<String> abilityNames = classService.getAvailableAbilities(classType)
                    .stream().map(Ability::getName).collect(Collectors.toList());
            String[] options = abilityNames.toArray(new String[0]);
            int slots = view.getAbilityCount();
            for (int i = 1; i <= slots; i++) {
                view.setAbilityOptions(i, options);
            }
        } catch (Exception e) {
            clearAbilityOptions();
        }
    }

    // --- Helper: Find Player by Name ---
    private Player getPlayerByName(String playerName) {
        return gameManagerController.getPlayers().stream()
                .filter(p -> p.getName().equalsIgnoreCase(playerName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerName));
    }

    // --- Helper: Ensure all abilities selected are unique ---
    private static boolean areDistinct(String[] items) {
        return Arrays.stream(items)
                .filter(item -> item != null)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .count() == items.length;
    }
}
