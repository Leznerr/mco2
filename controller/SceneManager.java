package controller;

import java.awt.CardLayout;
import java.awt.Container;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import model.core.Character;
import model.core.Player;
import model.util.DialogUtils;
import model.util.GameException;
import persistence.GameData;
import persistence.SaveLoadService;
import view.BattleView;
import view.CharacterManagementMenuView;
import view.HallOfFameManagementView;
import controller.HallOfFameController;
import view.MainMenuView;
import view.NewPlayersRegistrationView;
import view.PlayerCharacterManagementView;
import view.PlayerRegistrationView;
import view.SavedPlayersRegistrationView;

public final class SceneManager {

    // ---------- Shared Stage and Layout ----------
    private final JFrame stage;         // Single window
    private final CardLayout cards;     // For swapping views
    private final Container root;       // Content pane shortcut

    // ---------- Card Identifiers ----------
    private static final String CARD_MAIN_MENU  = "mainMenu";
    private static final String CARD_PLAYER_REG = "playerReg";
    private static final String CARD_NEW_PLAYER_REG = "newPlayerReg";
    private static final String CARD_SAVED_PLAYER_REG = "savedPlayerReg";
    private static final String CARD_HALL_OF_FAME = "hallOfFame";
    private static final String CARD_CHARACTER_MENU = "characterMenu";
    private static final String CARD_PLAYER_CHARACTER = "playerCharacter";
    private static final String CARD_BATTLE = "battle";

    // ---------- Cached View Instances ----------
    private MainMenuView mainMenuView;
    private PlayerRegistrationView playerRegView;
    private NewPlayersRegistrationView newPlayersRegView;
    private SavedPlayersRegistrationView savedPlayersRegView;
    private HallOfFameManagementView hallOfFameView;
    private CharacterManagementMenuView characterMenuView;
    private PlayerCharacterManagementView playerCharacterView;
    private BattleView battleView;

    private GameManagerController gameManagerController; // Keep the controller instance here
    private HallOfFameController hallOfFameController;

    // ---------- Constructor ----------
    public SceneManager() {
        stage = new JFrame("Fatal Fantasy: Tactics");
        cards = new CardLayout();
        root = stage.getContentPane();
        root.setLayout(cards);

        stage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        stage.setSize(800, 700);
        stage.setLocationRelativeTo(null);
        stage.setResizable(false);

        // Show the main menu as the initial screen
        showMainMenu();
        stage.setVisible(true);
    }

    // ---------- Navigation Methods ----------

    /** Displays the main menu screen. */
    public void showMainMenu() {
        if (mainMenuView == null) {
            mainMenuView = new MainMenuView();

            }
            mainMenuView.setActionListener(gameManagerController);
            root.add(mainMenuView, CARD_MAIN_MENU);
        }
        cards.show(root, CARD_MAIN_MENU);
    }

    /** Displays the Player Registration screen. */
    public void showPlayerRegistration() {
        if (playerRegView == null) {
            playerRegView = new PlayerRegistrationView();
            playerRegView.setActionListener(e -> {
                String cmd = e.getActionCommand();
                switch (cmd) {
                    case PlayerRegistrationView.NEW_PLAYERS -> showNewPlayersRegistration();
                    case PlayerRegistrationView.SAVED_PLAYERS -> showSavedPlayersRegistration();
                    case PlayerRegistrationView.RETURN_TO_MENU -> showMainMenu();
                }
            });
            root.add(playerRegView, CARD_PLAYER_REG);
        }
        cards.show(root, CARD_PLAYER_REG);
    }

    /** Shows the new players registration form. */
    public void showNewPlayersRegistration() {
        if (newPlayersRegView == null) {
            newPlayersRegView = new NewPlayersRegistrationView();
            newPlayersRegView.setActionListener(e -> {
                String cmd = e.getActionCommand();
                if (NewPlayersRegistrationView.RETURN.equals(cmd)) {
                    showPlayerRegistration();
                } else if (NewPlayersRegistrationView.REGISTER.equals(cmd)) {
                    String p1 = newPlayersRegView.getPlayer1Name();
                    String p2 = newPlayersRegView.getPlayer2Name();
                    if (p1.isEmpty() || p2.isEmpty()) {
                        JOptionPane.showMessageDialog(newPlayersRegView, "Both player names must be entered.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (gameManagerController.handleRegisterPlayers(p1, p2)) {
                        JOptionPane.showMessageDialog(newPlayersRegView,
                                "Players Registered: " + p1 + " and " + p2,
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        showMainMenu();
                    } else {
                        newPlayersRegView.resetFields();
                    }
                }
            });
            root.add(newPlayersRegView, CARD_NEW_PLAYER_REG);
        }
        newPlayersRegView.resetFields();
        cards.show(root, CARD_NEW_PLAYER_REG);
    }

    /** Shows the saved players registration form. */
    public void showSavedPlayersRegistration() {
        if (savedPlayersRegView == null) {
            savedPlayersRegView = new SavedPlayersRegistrationView();
            savedPlayersRegView.setActionListener(e -> {
                String cmd = e.getActionCommand();
                if (SavedPlayersRegistrationView.RETURN.equals(cmd)) {
                    showPlayerRegistration();
                } else if (SavedPlayersRegistrationView.REGISTER.equals(cmd)) {
                    String n1 = savedPlayersRegView.getSelectedPlayer1();
                    String n2 = savedPlayersRegView.getSelectedPlayer2();
                    if (n1 == null || n2 == null) {
                        JOptionPane.showMessageDialog(savedPlayersRegView, "Both players must be selected.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (gameManagerController.handleRegisterSavedPlayers(n1, n2)) {
                        JOptionPane.showMessageDialog(savedPlayersRegView,
                                "Players Loaded: " + n1 + " and " + n2,
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        showMainMenu();
                    }
                }
            });
            root.add(savedPlayersRegView, CARD_SAVED_PLAYER_REG);
        }
        try {
            GameData data = SaveLoadService.loadGame();
            List<Player> ps = data.getAllPlayers();
            String[] opts = ps.stream().map(Player::getName).toArray(String[]::new);
            savedPlayersRegView.setPlayer1Options(opts);
            savedPlayersRegView.setPlayer2Options(opts);
            savedPlayersRegView.resetDropdowns();
        } catch (GameException e) {
            JOptionPane.showMessageDialog(stage, "Failed to load saved players: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        cards.show(root, CARD_SAVED_PLAYER_REG);
    }

    /** Displays the Hall of Fame screen. */
    public void showHallOfFameManagement() {
        if (hallOfFameView == null) {
            hallOfFameView = new HallOfFameManagementView();
<<<
        }
        cards.show(root, CARD_HALL_OF_FAME);
    }

    /** Shows the menu to pick which player's characters to manage. */
    public void showCharacterManagementMenu(List<Player> players) {
        if (characterMenuView == null) {
            characterMenuView = new CharacterManagementMenuView();
            new CharacterManagementMenuController(characterMenuView, players, this);
            root.add(characterMenuView, CARD_CHARACTER_MENU);
        }
        cards.show(root, CARD_CHARACTER_MENU);
    }

    /** Shows character management options for a specific player. */
    public void showPlayerCharacterManagement(Player player) {
        playerCharacterView = new PlayerCharacterManagementView(playersIndex(player));
        new PlayerCharacterManagementController(playerCharacterView, player, gameManagerController);
        root.add(playerCharacterView, CARD_PLAYER_CHARACTER);
        cards.show(root, CARD_PLAYER_CHARACTER);
    }

    /** FIXED: Shows character management screen for a single player (called by GameManagerController). */
    public void showCharacterManagement(Player player) {
        playerCharacterView = new PlayerCharacterManagementView(playersIndex(player));
        new PlayerCharacterManagementController(playerCharacterView, player, gameManagerController);
        root.add(playerCharacterView, CARD_PLAYER_CHARACTER);
        cards.show(root, CARD_PLAYER_CHARACTER);
    }

    private int playersIndex(Player player) {
        List<Player> list = gameManagerController.getPlayers();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == player) return i + 1;
        }
        return 1;
    }

    /** Displays a battle between two characters. */
    public void showPlayerVsBotBattle(Player humanPlayer, Character human, Character bot, AIController aiController) {
        battleView = new BattleView(BattleView.BATTLE_PVB);
        try {
            BattleController battleController = new BattleController(battleView, gameManagerController, humanPlayer, null);
            battleView.setActionListener(e -> {
                String cmd = e.getActionCommand();
                if (BattleView.P1_USE.equals(cmd)) {
                    String ability = battleView.getSelectedAbility(1);
                    if (ability != null) {
                        for (var ab : human.getAbilities()) {
                            if (ab.getName().equals(ability)) {
                                try {
                                    battleController.submitMove(human, new model.battle.AbilityMove(ab));
                                } catch (GameException ex) {
                                    DialogUtils.showErrorDialog("Battle Error", ex.getMessage());
                                }
                                break;
                            }
                        }
                    }
                } else if (BattleView.RETURN.equals(cmd)) {
                    battleView.dispose();
                    showMainMenu();
                }
            });
            battleController.startBattleVsBot(human, bot, aiController);
        } catch (GameException e) {
            JOptionPane.showMessageDialog(stage, "Unable to start battle: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            battleView.dispose();
        }
    }

    /** Entry point for testing this class in isolation. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SceneManager::new);
    }

    /** Closes any standalone window. */
    public void closeWindow(java.awt.Window window) {
        if (window != null) {
            window.dispose();
        }
    }

    /** Launches the main menu — for external triggering. */
    public void start() {
        showMainMenu();
    }
}
