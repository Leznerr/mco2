package controller;

import model.core.Player;
import model.util.GameException;
import model.util.InputValidator;
import persistence.GameData;
import persistence.SaveLoadService;
import view.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        this.players = new ArrayList<>();

        bindUI();
    }

    /** Wires this controller to the main menu buttons. */
    private void bindUI() {
        mainMenuView.setController(this);
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
                for (Player player : players) {
                    sceneManager.showCharacterManagement(player); // Show Character Management View
                }
                mainMenuView.dispose(); // Close the MainMenuView
            }
        }
        case MainMenuView.ACTION_HALL_OF_FAME -> {
            sceneManager.showHallOfFameManagement(); // Show Hall of Fame View
            mainMenuView.dispose(); // Close the MainMenuView
        }
        case MainMenuView.ACTION_START_BATTLE -> {
            JOptionPane.showMessageDialog(mainMenuView, "Battle feature not implemented yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
            // Ideally, you should call sceneManager.showBattleView() here when ready
        }
        case MainMenuView.ACTION_EXIT -> {
            handleSaveGameRequest();
            mainMenuView.dispose(); // Close the MainMenuView
            System.exit(0); // Close the application
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
                        navigateBackToMainMenu();
                        view.dispose();
                    }
                }
            });
        });
    }

    
    private void handleNavigateToManualCreation(String playerName) {
        SwingUtilities.invokeLater(() -> {
            CharacterManualCreationView manualView = new CharacterManualCreationView(playerName);
            CharacterManualCreationController controller =
                new CharacterManualCreationController(manualView, playerName, this);
            manualView.setController(controller);
        });
    }

    private void handleNavigateToAutoCreation(String playerName) {
        SwingUtilities.invokeLater(() -> {
            CharacterAutoCreationView autoView = new CharacterAutoCreationView(playerName);
            CharacterAutoCreationController controller =
                new CharacterAutoCreationController(autoView, playerName, this);
            autoView.setController(controller);
        });
    }

    public void handleNavigateToCharacterManagement(Player player) {
        InputValidator.requireNonNull(player, "player");
        SwingUtilities.invokeLater(() -> sceneManager.showCharacterManagement(player));
    }


       // Method to handle player registration
    public void handleRegisterPlayers(String player1Name, String player2Name) {
        // Create new Player objects with the provided names
        Player player1 = new Player(player1Name);
        Player player2 = new Player(player2Name);

        // Load existing game data (or create new if no data exists)
        GameData gameData = SaveLoadService.loadGame();

        // Add the new players to the game data
        gameData.getAllPlayers().add(player1);
        gameData.getAllPlayers().add(player2);

        // Save the updated game data with the new players
        SaveLoadService.saveGame(gameData);  // Save the game data with the newly added players

        System.out.println("Players " + player1Name + " and " + player2Name + " have been registered.");
    }

    public void handleNavigateToHallOfFame() {
        SwingUtilities.invokeLater(() -> {
            HallOfFameManagementView view = new HallOfFameManagementView();
            HallOfFameController controller = new HallOfFameController(view);
            view.setController(controller);
        });
    }

    public void navigateBackToMainMenu() {
        SwingUtilities.invokeLater(() -> mainMenuView.setVisible(true));
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
}
