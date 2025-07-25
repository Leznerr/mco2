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
import model.util.RandomCharacterGenerator;
import model.util.SimpleBot;
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
        mainMenuView.setActionListener(this);
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
        case MainMenuView.REGISTER_PLAYERS -> {
            sceneManager.showPlayerRegistration(); // Shows Player Registration View
        }
        case MainMenuView.MANAGE_CHARACTERS -> {
            if (players.isEmpty()) {
                JOptionPane.showMessageDialog(mainMenuView, "Please register players first.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                sceneManager.showCharacterManagementMenu(players);
            }
        }
        case MainMenuView.HALL_OF_FAME -> {
            sceneManager.showHallOfFameManagement(); // Show Hall of Fame View
        }
        case MainMenuView.START_BATTLE -> {
            if (players.isEmpty() || players.get(0).getCharacters().isEmpty()) {
                JOptionPane.showMessageDialog(mainMenuView, "Please create a player and at least one character first.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Player player = players.get(0);
                Character human = player.getCharacters().get(0);
                try {
                    Character bot = RandomCharacterGenerator.generate("Bot");
                    AIController ai = new AIController(new SimpleBot(new java.util.Random()));
                    sceneManager.showPlayerVsBotBattle(player, human, bot, ai);
                } catch (GameException e1) {
                    JOptionPane.showMessageDialog(mainMenuView, "Failed to start battle: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        case MainMenuView.EXIT -> {
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
        int playerId = getPlayerIndexByName(playerName);
        SwingUtilities.invokeLater(() -> {
            CharacterCreationManagementView view = new CharacterCreationManagementView(playerId);

            view.setActionListener(e -> {
                String command = e.getActionCommand();
                switch (command) {
                    case CharacterCreationManagementView.MANUAL_CREATION -> {
                        handleNavigateToManualCreation(playerName);
                    }
                    case CharacterCreationManagementView.AUTO_CREATION -> {
                        handleNavigateToAutoCreation(playerName);
                    }
                    case CharacterCreationManagementView.RETURN -> {
                        navigateBackToMainMenu();
                    }
                }
            });

            // Menu is visible upon creation
        });
    }

    
    private void handleNavigateToManualCreation(String playerName) {
        int playerId = getPlayerIndexByName(playerName);
        SwingUtilities.invokeLater(() -> {
            CharacterManualCreationView manualView = new CharacterManualCreationView(playerId);
            new CharacterManualCreationController(manualView, playerName, this);
        });
    }

    private void handleNavigateToAutoCreation(String playerName) {
        int playerId = getPlayerIndexByName(playerName);
        SwingUtilities.invokeLater(() -> {
            CharacterAutoCreationView autoView = new CharacterAutoCreationView(playerId);
            new CharacterAutoCreationController(autoView, playerName, this);
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


    public void navigateBackToMainMenu() {
        SwingUtilities.invokeLater(sceneManager::showMainMenu);
    }

    /**
     * Handles all logic required when the application is requested to exit.
     */
    private void quitApplication() {
        handleSaveGameRequest();
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

    /** Deletes a player with the given name from the current session. */
    public void deletePlayerByName(String name) throws GameException {
        InputValidator.requireNonNull(name, "name");
        boolean removed = players.removeIf(p -> p.getName().equals(name));
        if (!removed) {
            throw new GameException("Player not found: " + name);
        }
    }

    /** Returns the current game data snapshot for persistence. */
    public GameData getGameData() throws GameException {
        return new GameData(players, hallOfFameController.getHallOfFame());
    }

    /** Returns the 1-based index of the player with the given name. */
    private int getPlayerIndexByName(String name) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equalsIgnoreCase(name)) {
                return i + 1;
            }
        }
        return 1;
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

            winner.incrementWins();
            hallOfFameController.addWinForPlayer(winner);

            if (winner.getCumulativeWins() % Constants.WINS_PER_REWARD == 0) {
                MagicItem reward = MagicItemFactory.createRandomReward();
                character.getInventory().addItem(reward);
            }

            SaveLoadService.saveGame(new GameData(players,
                                                 hallOfFameController.getHallOfFame()));
        } catch (GameException e) {
            JOptionPane.showMessageDialog(mainMenuView,
                    "Failed to record win: " + e.getMessage(),
                    "Win Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
