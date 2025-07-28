package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import app.Main;
import model.core.Character;
import model.core.Player;
import model.item.MagicItem;
import model.service.MagicItemFactory;
import model.util.Constants;
import model.util.GameException;
import model.util.InputValidator;
import persistence.GameData;
import persistence.SaveLoadService;
import view.CharacterAutoCreationView;
import view.CharacterCreationManagementView;
import view.CharacterManualCreationView;
import view.MainMenuView;

/**
 * Central controller for managing players, navigation, and game save/load in
 * Fatal Fantasy: Tactics (MCO2).
 *
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Handles main menu navigation and controller wiring.</li>
 *   <li>Manages registered players and their data.</li>
 *   <li>Coordinates save/load via persistence layer.</li>
 *   <li>Integrates with Hall of Fame, SceneManager, and character management.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Strictly framework-agnostic, modular, and production-ready.
 * </p>
 */
public final class GameManagerController implements ActionListener {

    private final List<Player> players;
    private final SceneManager sceneManager;
    private final HallOfFameController hallOfFameController;
    private final MainMenuView mainMenuView;

    /**
     * Constructs the main game controller.
     *
     * @param sceneManager          The scene manager to switch views. (non-null)
     * @param hallOfFameController  The controller for Hall of Fame. (non-null)
     * @param mainMenuView          The main menu view. (non-null)
     * @throws GameException If loading game data fails.
     */
    public GameManagerController(SceneManager sceneManager,
                                HallOfFameController hallOfFameController,
                                MainMenuView mainMenuView) throws GameException {
        InputValidator.requireNonNull(sceneManager, "sceneManager");
        InputValidator.requireNonNull(hallOfFameController, "hallOfFameController");
        InputValidator.requireNonNull(mainMenuView, "mainMenuView");

        this.sceneManager = sceneManager;
        this.hallOfFameController = hallOfFameController;
        this.mainMenuView = mainMenuView;

        GameData gameData = SaveLoadService.loadGame();
        this.players = new ArrayList<>(gameData.getAllPlayers());

        bindUI();
    }

    /** Wires this controller to the main menu buttons. */
    private void bindUI() {
        mainMenuView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        mainMenuView,
                        "Are you sure you want to quit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    quitApplication();
                }
            }
        });
    }

    /** Main button dispatcher for MainMenuView. */
  @Override
public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();

    switch (command) {
        case MainMenuView.ACTION_REGISTER_PLAYERS -> {
            sceneManager.showPlayerRegistration(); // Shows Player Registration View
            mainMenuView.dispose(); // Close the MainMenuView
        }
        case MainMenuView.ACTION_MANAGE_CHARACTERS -> {
            if (players.isEmpty()) {
                JOptionPane.showMessageDialog(mainMenuView, "Please register players first.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                sceneManager.showCharacterManagementMenu(players);
                mainMenuView.dispose();
            }
        }
        case MainMenuView.ACTION_HALL_OF_FAME -> showHallOfFameScreen();
        case MainMenuView.ACTION_TRADING_HALL -> {
            if (players.isEmpty()) {
                JOptionPane.showMessageDialog(mainMenuView, "Please register players first.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                sceneManager.showTradingHall(players);
                mainMenuView.dispose();
            }
        }
        case MainMenuView.ACTION_START_BATTLE -> {
            if (players.isEmpty()) {
                JOptionPane.showMessageDialog(mainMenuView, "Please register players first.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                sceneManager.showBattleModes(players);
                mainMenuView.dispose();
            }
        }
        case MainMenuView.ACTION_EXIT -> {
            quitApplication();
        }
        default -> {
            JOptionPane.showMessageDialog(mainMenuView, "Unknown action: " + command, "Unknown Action", JOptionPane.WARNING_MESSAGE);
        }
    }
}

    // === Navigation & Controller Binding Methods ===

    /**
     * Opens character creation management for a given player (by ID or name).
     * Called from UI navigation.
     *
     * @param playerID the ID or name of the player to manage
     */
    public void handleNavigateToCharacterCreationManagement(String playerName) {
        SwingUtilities.invokeLater(() -> {
            CharacterCreationManagementView view = new CharacterCreationManagementView(playerName);

            view.setActionListener(e -> {
                String command = e.getActionCommand();
                switch (command) {
                    case CharacterCreationManagementView.MANUAL_CREATION -> {
                        handleNavigateToManualCreation(playerName);
                        view.dispose();
                    }
                    case CharacterCreationManagementView.AUTO_CREATION -> {
                        handleNavigateToAutoCreation(playerName);
                        view.dispose();
                    }
                    case CharacterCreationManagementView.RETURN -> {
                        Player player = findByName(players, playerName);
                        if (player != null) {
                            handleNavigateToCharacterManagement(player);
                        } else {
                            navigateBackToMainMenu();
                        }
                        view.dispose();
                    }
                }
            });

            // Ensure the menu becomes visible when opened
            view.setVisible(true);
        });
    }

    
    private void handleNavigateToManualCreation(String playerName) {
        SwingUtilities.invokeLater(() -> {
            CharacterManualCreationView manualView = new CharacterManualCreationView(playerName);
            CharacterManualCreationController controller =
                new CharacterManualCreationController(manualView, playerName, this);
            manualView.setController(controller);
            manualView.setVisible(true);
        });
    }

    private void handleNavigateToAutoCreation(String playerName) {
        SwingUtilities.invokeLater(() -> {
            CharacterAutoCreationView autoView = new CharacterAutoCreationView(playerName);
            CharacterAutoCreationController controller =
                new CharacterAutoCreationController(autoView, playerName, this);
            autoView.setController(controller);
            autoView.setVisible(true);
        });
    }

    public void handleNavigateToCharacterManagement(Player player) {
        InputValidator.requireNonNull(player, "player");
        SwingUtilities.invokeLater(() -> sceneManager.showCharacterManagement(player));
    }


    public boolean handleRegisterPlayers(String player1Name, String player2Name) {
        boolean hasConflict = false;
        StringBuilder errorMsg = new StringBuilder();

        if (player1Name.equalsIgnoreCase(player2Name)) {
            errorMsg.append("Player names must be unique.\n");
            hasConflict = true;
        }

        GameData gameData = SaveLoadService.loadGame();
        List<Player> existing = new ArrayList<>(gameData.getAllPlayers());

        int duplicateCount = 0;
        for (Player p : existing) {
            boolean match1 = p.getName().equalsIgnoreCase(player1Name);
            boolean match2 = p.getName().equalsIgnoreCase(player2Name);
            if (match1 || match2) {
                duplicateCount++;
            }
        }

        if (duplicateCount > 0) {
            errorMsg.append("One or both player names already exist in saved data.\n");
            hasConflict = true;
        }

        if (!hasConflict) {
            Player player1 = new Player(player1Name);
            Player player2 = new Player(player2Name);
            existing.add(player1);
            existing.add(player2);
            gameData.setAllPlayers(existing);
            SaveLoadService.saveGame(gameData);
            players.clear();
            players.addAll(existing);
            System.out.println("Players " + player1Name + " and " + player2Name + " have been registered.");
            return true;
        } else {
            JOptionPane.showMessageDialog(null, errorMsg.toString(), "Player Registration Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Loads two previously saved players and sets them as active for this session.
     *
     * @param player1Name name of first player
     * @param player2Name name of second player
     * @return {@code true} if players were loaded successfully
     */
    public boolean handleRegisterSavedPlayers(String player1Name, String player2Name) {
        StringBuilder errorMsg = new StringBuilder();
        if (player1Name == null || player2Name == null || player1Name.isBlank() || player2Name.isBlank()) {
            errorMsg.append("Both players must be selected.\n");
        }
        if (player1Name != null && player1Name.equalsIgnoreCase(player2Name)) {
            errorMsg.append("Player names must be unique.\n");
        }

        if (!errorMsg.isEmpty()) {
            JOptionPane.showMessageDialog(null, errorMsg.toString(), "Player Selection Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            GameData data = SaveLoadService.loadGame();
            List<Player> all = data.getAllPlayers();
            Player p1 = findByName(all, player1Name);
            Player p2 = findByName(all, player2Name);

            if (p1 == null || p2 == null) {
                JOptionPane.showMessageDialog(null, "Selected players could not be loaded.", "Player Selection Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            players.clear();
            players.add(p1);
            players.add(p2);
            // ensure persistence unaffected but save to ensure data file exists
            SaveLoadService.saveGame(data);
            return true;
        } catch (GameException e) {
            JOptionPane.showMessageDialog(null, "Failed to load players: " + e.getMessage(), "Player Selection Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static Player findByName(List<Player> list, String name) {
        for (Player p : list) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Navigates to the Hall of Fame management screen.
     */
    public void showHallOfFameScreen() {
        SwingUtilities.invokeLater(() -> {
            try {
                sceneManager.showHallOfFameManagement();
                mainMenuView.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainMenuView,
                        "Unable to open Hall of Fame: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }


    public void navigateBackToMainMenu() {
        SwingUtilities.invokeLater(sceneManager::showMainMenu);
    }

    /**
     * Handles all logic required when the application is requested to exit.
     */
    private void quitApplication() {
        handleSaveGameRequest();
        mainMenuView.dispose();
        Main.shutdown();
    }

    // === Game Data Save/Load Methods ===

    /**
     * Saves game state in a background thread.
     */
    public void handleSaveGameRequest() {
        new Thread(() -> {
            try {
                SaveLoadService.saveGame(
                        new GameData(players, hallOfFameController.getHallOfFame())
                );
            } catch (GameException e) {
                // Production: Consider logging
                JOptionPane.showMessageDialog(mainMenuView, "Failed to save game: " + e.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    /**
     * Loads game state in a background thread.
     */
    public void handleLoadGameRequest() {
        new Thread(() -> {
            try {
                GameData data = SaveLoadService.loadGame();
                if (data != null) {
                    players.clear();
                    players.addAll(data.getAllPlayers());
                    hallOfFameController.setHallOfFame(data.getHallOfFame());
                }
            } catch (GameException e) {
                // Production: Consider logging
                JOptionPane.showMessageDialog(mainMenuView, "Failed to load game: " + e.getMessage(),
                        "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    /**
     * Returns an unmodifiable list of all registered players.
     *
     * @return List of all players (never null, unmodifiable)
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Returns a snapshot of current game data used for persistence.
     */
    public GameData getGameData() throws GameException {
        return new GameData(players, hallOfFameController.getHallOfFame());
    }

    /**
     * Deletes a player identified by name from the current game state.
     *
     * @param name the player name to remove
     * @throws GameException if the player does not exist
     */
    public void deletePlayerByName(String name) throws GameException {
        boolean removed = players.removeIf(p -> p.getName().equals(name));
        if (!removed) {
            throw new GameException("Player not found: " + name);
        }
    }

    /**
     * Processes a player's win: increments wins, awards Hall of Fame credit,
     * and grants a random magic item every {@link Constants#WINS_PER_REWARD}
     * victories. The new item is added to the winning character's inventory
     * and persisted via {@link SaveLoadService}.
     *
     * @param winner   the player who won
     * @param character the character that secured the win
     */
    public void handlePlayerWin(Player winner, Character character) {
        try {
            InputValidator.requireNonNull(winner, "winner");
            InputValidator.requireNonNull(character, "character");

            // Skip Hall of Fame updates for AI-controlled bots
            if ("Bot".equalsIgnoreCase(winner.getName())) {
                return;
            }

            winner.incrementWins();
            character.incrementBattlesWon();
            hallOfFameController.addWinForPlayer(winner);
            hallOfFameController.addWinForCharacter(character);

            if (character.getBattlesWon() % Constants.WINS_PER_REWARD == 0) {
                MagicItem reward = generateUniqueReward(character);
                character.getInventory().addItem(reward);
                javax.swing.JOptionPane.showMessageDialog(null,
                        "New Magic Item awarded: " + reward.getName() +
                        "\n" + reward.getDescription(),
                        "Item Awarded",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }

            SaveLoadService.saveGame(new GameData(players,
                                                 hallOfFameController.getHallOfFame()));
        } catch (GameException e) {
            JOptionPane.showMessageDialog(mainMenuView,
                    "Failed to record win: " + e.getMessage(),
                    "Win Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Generates a random magic item not already held by the character, if
     * possible. Falls back to any random item after several attempts.
     */
    private MagicItem generateUniqueReward(Character character) {
        java.util.List<MagicItem> owned = character.getInventory().getAllItems();
        MagicItem reward = MagicItemFactory.createRandomReward();
        int attempts = 0;
        while (owned.contains(reward) && attempts < 10) {
            reward = MagicItemFactory.createRandomReward();
            attempts++;
        }
        return reward;
    }
}
