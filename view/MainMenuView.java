package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import controller.GameManagerController;

/**
 * The main menu view for Fatal Fantasy: Tactics Game.
 */
public class MainMenuView extends JFrame {
    // Action commands for controller to respond to
    public static final String ACTION_REGISTER_PLAYERS = "ACTION_REGISTER_PLAYERS";
    public static final String ACTION_MANAGE_CHARACTERS = "ACTION_MANAGE_CHARACTERS";
    public static final String ACTION_HALL_OF_FAME = "ACTION_HALL_OF_FAME";
    public static final String ACTION_START_BATTLE = "ACTION_START_BATTLE";
    public static final String ACTION_EXIT = "ACTION_EXIT";

    // UI Labels
    private static final String LABEL_REGISTER_PLAYERS = "Register Players";
    private static final String LABEL_MANAGE_CHARACTERS = "Manage Characters";
    private static final String LABEL_HALL_OF_FAME = "Hall Of Fame";
    private static final String LABEL_START_BATTLE = "Start Battle";
    private static final String LABEL_EXIT = "Exit";

    // UI components
    private JButton btnRegisterPlayers;
    private JButton btnManageCharacters;
    private JButton btnHallOfFame;
    private JButton btnStartBattle;
    private JButton btnExit;

    /**
     * Constructs the Main Menu UI of Fatal Fantasy: Tactics Game.
     */
    public MainMenuView() {
        super("Fatal Fantasy: Tactics | Main Menu");

        initUI();

        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    MainMenuView.this,
                    "Are you sure you want to quit?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    dispose(); 
                    System.exit(0);
                }
            }
        });

        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Initializes the UI components and arranges them in the main layout.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private final Image bg = new ImageIcon("view/assets/MainMenuBG.jpg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int imgWidth = bg.getWidth(this);
                int imgHeight = bg.getHeight(this);
                double scale = Math.max(
                    panelWidth / (double) imgWidth,
                    panelHeight / (double) imgHeight
                );
                int width = (int) (imgWidth * scale);
                int height = (int) (imgHeight * scale);
                int x = (panelWidth - width) / 2;
                int y = (panelHeight - height) / 2;
                g.drawImage(bg, x, y, width, height, this);
            }
        };

        

        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));
        backgroundPanel.add(Box.createVerticalStrut(40));

        // Logo
        ImageIcon logoIcon = new ImageIcon("view/assets/MainLogoBG2.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(300, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(logoLabel);
        backgroundPanel.add(Box.createVerticalStrut(40));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        btnRegisterPlayers = new RoundedButton(LABEL_REGISTER_PLAYERS);
        btnRegisterPlayers.setActionCommand(ACTION_REGISTER_PLAYERS);

        btnManageCharacters = new RoundedButton(LABEL_MANAGE_CHARACTERS);
        btnManageCharacters.setActionCommand(ACTION_MANAGE_CHARACTERS);

        btnHallOfFame = new RoundedButton(LABEL_HALL_OF_FAME);
        btnHallOfFame.setActionCommand(ACTION_HALL_OF_FAME);

        btnStartBattle = new RoundedButton(LABEL_START_BATTLE);
        btnStartBattle.setActionCommand(ACTION_START_BATTLE);

        btnExit = new RoundedButton(LABEL_EXIT);
        btnExit.setActionCommand(ACTION_EXIT);

        buttonPanel.add(btnRegisterPlayers);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnManageCharacters);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnHallOfFame);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnStartBattle);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnExit);

        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(buttonPanel);
        backgroundPanel.add(Box.createVerticalGlue());

        setContentPane(backgroundPanel);
    }

    /**
     * Sets the action listener for the button click events.
     * 
     * @param listener The ActionListener (usually a controller)
     */
    public void setActionListener(ActionListener listener) {
        btnRegisterPlayers.addActionListener(listener);
        btnManageCharacters.addActionListener(listener);
        btnHallOfFame.addActionListener(listener);
        btnStartBattle.addActionListener(listener);
        btnExit.addActionListener(listener);
    }

    /**
     * Delegates action handling to the controller.
     */
    public void setController(GameManagerController controller) {
        setActionListener(controller); // Assumes controller implements ActionListener
    }

    /** Dialog helpers */
    public void showInfoMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

       // Getter methods for each button
    public JButton getRegisterPlayersButton() {
        return btnRegisterPlayers;
    }

    public JButton getManageCharactersButton() {
        return btnManageCharacters;
    }

    public JButton getHallOfFameButton() {
        return btnHallOfFame;
    }

    public JButton getStartBattleButton() {
        return btnStartBattle;
    }

    public JButton getExitButton() {
        return btnExit;
    }
    
}
