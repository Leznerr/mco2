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
import view.CharacterManualCreationView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

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
        view.setAbilityOptions(1, new String[0]);
        view.setAbilityOptions(2, new String[0]);
        view.setAbilityOptions(3, new String[0]);
    }

    // --- UI Event Binding ---
    private void bindUI() {
        view.addCreateCharacterListener(_ -> handleCreateCharacter());
        view.addReturnListener(_-> view.dispose());
        view.addClassDropdownListener(_ -> handleClassSelection());
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
            InputValidator.requireSize(selectedAbilityNames, 3, 3, "You must choose exactly 3 abilities.");

            // Validate abilities are unique
            if (!areDistinct(selectedAbilityNames)) {
                throw new GameException("All three selected abilities must be unique.");
            }

            // Convert to enums/objects
            RaceType race = RaceType.valueOf(raceStr);
            ClassType classType = ClassType.valueOf(classStr);
            List<Ability> abilities = classService.getAbilitiesByNames(selectedAbilityNames);

            // Business rule: abilities must match class!
            List<String> allowedAbilityNames = classService.getAvailableAbilities(classType)
                    .stream().map(Ability::getName).collect(Collectors.toList());
            for (String abilityName : selectedAbilityNames) {
                if (!allowedAbilityNames.contains(abilityName)) {
                    throw new GameException("Ability \"" + abilityName + "\" is not valid for the selected class.");
                }
            }

            // Attach character to player
            Character newCharacter = new Character(name, race, classType);
            newCharacter.setAbilities(abilities);

            Player player = getPlayerByName(this.playerName);
            player.addCharacter(newCharacter);

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
            view.setAbilityOptions(1, options);
            view.setAbilityOptions(2, options);
            view.setAbilityOptions(3, options);
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
