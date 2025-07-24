package controller;

import java.awt.CardLayout;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import model.core.Player;
import view.CharacterManagementView;
import view.HallOfFameManagementView;
import view.MainMenuView;
import view.PlayerRegistrationView;
import view.BattleView;

/**
 * Central navigator that swaps View cards inside ONE shared JFrame.
 * Handles screen transitions for the GUI in Fatal Fantasy: Tactics.
 */
public final class SceneManager {

    /* ---------- Shared Stage and Layout ---------- */
    private final JFrame stage;         // Single window
    private final CardLayout cards;     // For swapping views
    private final Container root;       // Content pane shortcut

    /* ---------- Card Identifiers ---------- */
    private static final String CARD_MAIN_MENU  = "mainMenu";
    private static final String CARD_PLAYER_REG = "playerReg";
    private static final String CARD_HALL_OF_FAME = "hallOfFame";
    private static final String CARD_CHARACTER_MANAGEMENT = "characterManagement";
    private static final String CARD_BATTLE = "battle";

    /* ---------- Cached View Instances ---------- */
    private MainMenuView mainMenuView;
    private PlayerRegistrationView playerRegView;
    private HallOfFameManagementView hallOfFameView;
    private CharacterManagementView characterManagementView;
    private BattleView battleView;

    private GameManagerController gameManagerController; // Keep the controller instance here

    /* ---------- Constructor ---------- */
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

    /* ---------- Navigation Methods ---------- */

    /** Displays the main menu screen. */
    public void showMainMenu() {
        if (mainMenuView == null) {
            mainMenuView = new MainMenuView();

            // Initialize the controller only once
            hallOfFameView = new HallOfFameManagementView(); 
            if (gameManagerController == null) {
                gameManagerController = new GameManagerController(this, new HallOfFameController(hallOfFameView), mainMenuView);
            }

            mainMenuView.setActionListener(gameManagerController);  // Pass the controller for action handling
            root.add(mainMenuView.getContentPane(), CARD_MAIN_MENU);
        }

        cards.show(root, CARD_MAIN_MENU);
    }

 public void showPlayerRegistration() {
    if (playerRegView == null) {
        playerRegView = new PlayerRegistrationView();

        // Set up the action listener for button clicks
        playerRegView.setActionListener(e -> {
            String cmd = e.getActionCommand();
            switch (cmd) {
                case PlayerRegistrationView.RETURN -> {
                    // If "Return" is clicked, show the main menu
                    showMainMenu();
                }
                case PlayerRegistrationView.REGISTER -> {
                    // Handle player registration logic
                    String player1Name = playerRegView.getPlayer1Name(); // Get player 1 name from the view
                    String player2Name = playerRegView.getPlayer2Name(); // Get player 2 name from the view

                    // Validate the names (ensure they are not empty)
                    if (player1Name.isEmpty() || player2Name.isEmpty()) {
                        // Show an error message if names are empty
                        JOptionPane.showMessageDialog(playerRegView, "Both player names must be entered.", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // Register the players and save them to the file
                        GameManagerController controller = new GameManagerController(this, 
                                new HallOfFameController(new HallOfFameManagementView()), mainMenuView);
                        controller.handleRegisterPlayers(player1Name, player2Name); // Save the players

                        // Show confirmation message
                        JOptionPane.showMessageDialog(playerRegView, "Players Registered: " + player1Name + " and " + player2Name, 
                                "Success", JOptionPane.INFORMATION_MESSAGE);

                        System.out.println("Players Registered: " + player1Name + " and " + player2Name);

                        // After successful registration, return to the main menu
                        showMainMenu(); 
                    }
                }
            }
        });

        root.add(playerRegView.getContentPane(), CARD_PLAYER_REG);
    }

    // Reset fields before displaying the Player Registration View
    playerRegView.resetFields(); 
    cards.show(root, CARD_PLAYER_REG);
}



    /** Displays the Hall of Fame screen. */
    public void showHallOfFameManagement() {
        if (hallOfFameView == null) {
            hallOfFameView = new HallOfFameManagementView();
            HallOfFameController controller = new HallOfFameController(hallOfFameView);
            hallOfFameView.setActionListener(controller); // Set the controller to handle actions
            root.add(hallOfFameView.getContentPane(), CARD_HALL_OF_FAME);
        }

        cards.show(root, CARD_HALL_OF_FAME);
    }

    /** Displays the character management screen for the specified player. */
    public void showCharacterManagement(Player player) {
        if (characterManagementView == null) {
            characterManagementView = new CharacterManagementView(player);
            root.add(characterManagementView.getContentPane(), CARD_CHARACTER_MANAGEMENT);
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
