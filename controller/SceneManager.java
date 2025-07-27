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
import view.MainMenuView;
import view.NewPlayersRegistrationView;
import view.PlayerCharacterManagementView;
import view.PlayerDeleteView;
import view.PlayerRegistrationView;
import view.SavedPlayersRegistrationView;
import view.TradeView;
import view.TradingHallView;

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
    private static final String CARD_BATTLE_MODES = "battleModes";
    private static final String CARD_DELETE_PLAYER = "deletePlayer";
    private static final String CARD_TRADING_HALL = "tradingHall";
    private static final String CARD_TRADE_VIEW = "tradeView";

    // ---------- Cached View Instances ----------
    private MainMenuView mainMenuView;
    private PlayerRegistrationView playerRegView;
    private NewPlayersRegistrationView newPlayersRegView;
    private SavedPlayersRegistrationView savedPlayersRegView;
    private HallOfFameManagementView hallOfFameView;
    private CharacterManagementMenuView characterMenuView;
    private CharacterManagementMenuController characterMenuController;
    private PlayerCharacterManagementView playerCharacterView;
    private BattleView battleView;
    private PlayerDeleteView playerDeleteView;
    private PlayerDeleteController playerDeleteController;
    private view.BattleModesView battleModesView;
    private HallOfFameController hallOfFameController;
    private TradingHallView tradingHallView;
    private TradingHallController tradingHallController;
    private TradeView tradeView;
    private TradeController tradeController;

    private GameManagerController gameManagerController; // Keep the controller instance here

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
            // Initialize the controller only once
            hallOfFameView = new HallOfFameManagementView();
            hallOfFameController = new HallOfFameController(hallOfFameView, this);
            hallOfFameView.setActionListener(hallOfFameController);
            root.add(hallOfFameView.getContentPane(), CARD_HALL_OF_FAME);

            if (gameManagerController == null) {
                gameManagerController = new GameManagerController(this, hallOfFameController, mainMenuView);
            }
            mainMenuView.setActionListener(gameManagerController);
            root.add(mainMenuView.getContentPane(), CARD_MAIN_MENU);
        }
        stage.setSize(800, 700);
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
                    case PlayerRegistrationView.DELETE_PLAYER -> showPlayerDelete();
                    case PlayerRegistrationView.RETURN_TO_MENU -> showMainMenu();
                }
            });
            root.add(playerRegView.getContentPane(), CARD_PLAYER_REG);
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
            root.add(newPlayersRegView.getContentPane(), CARD_NEW_PLAYER_REG);
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
            root.add(savedPlayersRegView.getContentPane(), CARD_SAVED_PLAYER_REG);
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

    /** Displays the Player Deletion screen. */
    public void showPlayerDelete() {
        if (playerDeleteView == null) {
            playerDeleteView = new PlayerDeleteView();
            playerDeleteController = new PlayerDeleteController(playerDeleteView, gameManagerController, this);
            root.add(playerDeleteView.getContentPane(), CARD_DELETE_PLAYER);
        } else {
            playerDeleteController.refresh();
        }
        cards.show(root, CARD_DELETE_PLAYER);
    }

    /** Displays the Hall of Fame screen. */
    public void showHallOfFameManagement() {
        if (hallOfFameView == null) {
            hallOfFameView = new HallOfFameManagementView();
        }
        if (hallOfFameController == null) {
            hallOfFameController = new HallOfFameController(hallOfFameView, this);
            hallOfFameView.setActionListener(hallOfFameController);
        }
        if (hallOfFameView.getContentPane().getParent() == null) {
            root.add(hallOfFameView.getContentPane(), CARD_HALL_OF_FAME);
        }
        cards.show(root, CARD_HALL_OF_FAME);
    }

    /** Displays the Trading Hall screen. */
    public void showTradingHall(List<Player> players) {
        if (tradingHallView == null) {
            tradingHallView = new TradingHallView();
            tradingHallController = new TradingHallController(tradingHallView, players, this);
            root.add(tradingHallView.getContentPane(), CARD_TRADING_HALL);
        } else {
            tradingHallController.refresh();
        }
        stage.setSize(800, 700);
        cards.show(root, CARD_TRADING_HALL);
    }

    /**
     * Opens a separate {@link TradeView} window for the two selected players.
     * The Trading Hall view is disposed before launching the trade window.
     * When the trade concludes the main stage size is reset and the user is
     * returned to the Trading Hall.
     */
    public void showTradeView(Player merchant, Player client) {
        System.out.println("SceneManager.showTradeView merchant="
                + merchant.getName() + ", client=" + client.getName());

        if (tradingHallView != null) {
            tradingHallView.dispose();
        }
        stage.setVisible(false);

        model.core.Character mChar = merchant.getCharacters().isEmpty()
                ? null : merchant.getCharacters().getFirst();
        model.core.Character cChar = client.getCharacters().isEmpty()
                ? null : client.getCharacters().getFirst();

        tradeView = new TradeView(merchant, mChar, client, cChar);
        try {
            tradeController = new TradeController(tradeView,
                    gameManagerController.getPlayers());
        } catch (GameException e) {
            JOptionPane.showMessageDialog(stage,
                    "Unable to open trade view: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tradeView.setActionListener(e -> {
            String cmd = e.getActionCommand();
            if (TradeView.RETURN.equals(cmd)) {
                tradeView.dispose();
                stage.setSize(800, 700);
                stage.setVisible(true);
                showTradingHall(gameManagerController.getPlayers());
            }
        });

        tradeView.setVisible(true);
    }

    /** Shows the menu to pick which player's characters to manage. */
    public void showCharacterManagementMenu(List<Player> players) {
        if (characterMenuView == null) {
            characterMenuView = new CharacterManagementMenuView();
            characterMenuController = new CharacterManagementMenuController(characterMenuView, players, this);
            root.add(characterMenuView.getContentPane(), CARD_CHARACTER_MENU);
        } else if (characterMenuController != null) {
            characterMenuController.refresh();
        }
        cards.show(root, CARD_CHARACTER_MENU);
    }

    /** Shows character management options for a specific player. */
    public void showPlayerCharacterManagement(Player player) {
        playerCharacterView = new PlayerCharacterManagementView(playersIndex(player));
        new PlayerCharacterManagementController(playerCharacterView, player, gameManagerController);
        root.add(playerCharacterView.getContentPane(), CARD_PLAYER_CHARACTER);
        cards.show(root, CARD_PLAYER_CHARACTER);
    }

    /** FIXED: Shows character management screen for a single player (called by GameManagerController). */
    public void showCharacterManagement(Player player) {
        playerCharacterView = new PlayerCharacterManagementView(playersIndex(player));
        new PlayerCharacterManagementController(playerCharacterView, player, gameManagerController);
        root.add(playerCharacterView.getContentPane(), CARD_PLAYER_CHARACTER);
        cards.show(root, CARD_PLAYER_CHARACTER);
    }

    private int playersIndex(Player player) {
        List<Player> list = gameManagerController.getPlayers();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == player) return i + 1;
        }
        return 1;
    }

    /** Shows the battle mode selection screen. */
    public void showBattleModes(List<Player> players) {
        battleModesView = new view.BattleModesView();
        new BattleModesController(battleModesView, players, this, gameManagerController);
        root.add(battleModesView.getContentPane(), CARD_BATTLE_MODES);
        cards.show(root, CARD_BATTLE_MODES);
    }

    /** Displays a battle between two characters. */
    public void showPlayerVsBotBattle(Player humanPlayer, Character human, Character bot, AIController aiController) {
        battleView = new BattleView(BattleView.BATTLE_PVB, human, bot);
        try {
            BattleController battleController = new BattleController(battleView, gameManagerController, humanPlayer, null);
            battleView.addUseAbilityP1Listener(e -> {
                String choice = (String) battleView.getAbilitySelectorP1().getSelectedItem();
                if (choice != null) {
                    try {
                        Character user = battleController.getBattleCopy(human);
                        if (user != null) {
                            battleController.handlePlayerChoice(user, choice);
                        }
                    } catch (GameException ex) {
                        DialogUtils.showErrorDialog("Battle Error", ex.getMessage());
                        battleView.setBattleControlsEnabled(false);
                        battleView.setEndButtonsEnabled(true);
                    }
                }
            });
            battleView.addReturnListener(e -> {
                battleView.dispose();
                showMainMenu();
            });
            battleView.addRematchListener(e -> {
                try {
                    battleController.startBattleVsBot(human, bot, aiController);
                } catch (GameException ex) {
                    DialogUtils.showErrorDialog("Battle Error", ex.getMessage());
                }
            });
            root.add(battleView.getContentPane(), CARD_BATTLE);
            stage.setSize(1200, 700);
            cards.show(root, CARD_BATTLE);
            battleController.startBattleVsBot(human, bot, aiController);
        } catch (GameException e) {
            JOptionPane.showMessageDialog(stage, "Unable to start battle: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            battleView.dispose();
        }
    }

    /** Displays a PvP battle between two players. */
    public void showPlayerVsPlayerBattle(Player p1, Character c1, Player p2, Character c2) {
        battleView = new BattleView(c1, c2);
        battleView.setPlayer2ControlsEnabled(true);
        try {
            BattleController controller = new BattleController(battleView, gameManagerController, p1, p2);
            battleView.addUseAbilityP1Listener(e -> {
                String choice = (String) battleView.getAbilitySelectorP1().getSelectedItem();
                if (choice != null) {
                    try {
                        Character user = controller.getBattleCopy(c1);
                        if (user != null) {
                            controller.handlePlayerChoice(user, choice);
                        }
                    } catch (GameException ex) {
                        DialogUtils.showErrorDialog("Battle Error", ex.getMessage());
                        battleView.setBattleControlsEnabled(false);
                        battleView.setEndButtonsEnabled(true);
                    }
                }
            });
            battleView.addUseAbilityP2Listener(e -> {
                String choice = (String) battleView.getAbilitySelectorP2().getSelectedItem();
                if (choice != null) {
                    try {
                        Character user = controller.getBattleCopy(c2);
                        if (user != null) {
                            controller.handlePlayerChoice(user, choice);
                        }
                    } catch (GameException ex) {
                        DialogUtils.showErrorDialog("Battle Error", ex.getMessage());
                        battleView.setBattleControlsEnabled(false);
                        battleView.setEndButtonsEnabled(true);
                    }
                }
            });
            battleView.addReturnListener(e -> {
                battleView.dispose();
                showMainMenu();
            });
            battleView.addRematchListener(e -> {
                try {
                    controller.startBattle(c1, c2);
                } catch (GameException ex) {
                    DialogUtils.showErrorDialog("Battle Error", ex.getMessage());
                }
            });
            root.add(battleView.getContentPane(), CARD_BATTLE);
            stage.setSize(1200, 700);
            cards.show(root, CARD_BATTLE);
            controller.startBattle(c1, c2);
        } catch (GameException e) {
            JOptionPane.showMessageDialog(stage, "Unable to start battle: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            battleView.dispose();
        }
    }

    /** Entry point for testing this class in isolation. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SceneManager::new);
    }

    /** Launches the main menu — for external triggering. */
    public void start() {
        showMainMenu();
    }
}
