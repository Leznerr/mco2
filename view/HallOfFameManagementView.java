package view;

import controller.HallOfFameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The Hall of Fame management view for Fatal Fantasy: Tactics Game.
 */
public class HallOfFameManagementView extends JFrame {

    // Action Commands (used by controller)
    public static final String SHOW_TOP_PLAYERS = "SHOW_TOP_PLAYERS";
    public static final String SHOW_TOP_CHARACTERS = "SHOW_TOP_CHARACTERS";
    public static final String RETURN = "RETURN_TO_MENU";

    // Button Labels
    public static final String TOP_PLAYERS = "Top Players";
    public static final String TOP_CHARACTERS = "Top Characters";
    public static final String RETURN_LABEL = "Return to Menu";

    // UI components
    private JButton btnTopPlayers;
    private JButton btnTopCharacters;
    private JButton btnReturn;

    private HallOfFameController controller;

    /**
     * Constructs the Hall Of Fame Management UI of Fatal Fantasy: Tactics Game.
     */
    public HallOfFameManagementView() {
        super("Fatal Fantasy: Tactics | Hall Of Fame Management");
        initUI();

        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Confirm before closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    HallOfFameManagementView.this,
                    "Are you sure you want to quit?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    dispose(); // closes the window
                }
            }
        });

        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Initializes the UI layout, components, and backgrounds.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private final Image bg = new ImageIcon("view/assets/HallOfFameBG.jpg").getImage();

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
        backgroundPanel.add(Box.createVerticalStrut(60)); // top spacing

        // Logo
        ImageIcon logoIcon = new ImageIcon("view/assets/HallOfFameManagLogo.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(650, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(logoLabel);
        backgroundPanel.add(Box.createVerticalStrut(100));

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        btnTopPlayers = new RoundedButton(TOP_PLAYERS);
        btnTopCharacters = new RoundedButton(TOP_CHARACTERS);
        btnReturn = new RoundedButton(RETURN_LABEL);

        // Assign Action Commands
        btnTopPlayers.setActionCommand(SHOW_TOP_PLAYERS);
        btnTopCharacters.setActionCommand(SHOW_TOP_CHARACTERS);
        btnReturn.setActionCommand(RETURN);

        buttonPanel.add(btnTopPlayers);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnTopCharacters);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnReturn);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        backgroundPanel.add(buttonPanel);
        backgroundPanel.add(Box.createVerticalGlue()); // center vertically

        setContentPane(backgroundPanel);
    }

    /**
     * Assigns an action listener for the buttons.
     *
     * @param listener The listener (usually the controller)
     */
    public void setActionListener(ActionListener listener) {
        if (listener != null) {
            btnTopPlayers.addActionListener(listener);
            btnTopCharacters.addActionListener(listener);
            btnReturn.addActionListener(listener);
        }
    }

    /**
     * Sets the controller and binds it as the action listener.
     *
     * @param controller The HallOfFameController
     */
    public void setController(HallOfFameController controller) {
        this.controller = controller;
        setActionListener(controller);
    }


    
    /**
     * Displays an informational message to the user.
     */
    public void showInfoMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays an error message to the user.
     */
    public void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
