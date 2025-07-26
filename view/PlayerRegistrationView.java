package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// import controller._;

/**
 * The player registration view for Fatal Fantasy: Tactics Game.
 */
public class PlayerRegistrationView extends JPanel {
    // Button labels
    public static final String NEW_PLAYERS = "New Players";
    public static final String SAVED_PLAYERS = "Saved Players";
    public static final String DELETE_PLAYER = "Delete Player";
    public static final String RETURN_TO_MENU = "Return to Menu";

    // UI components
    private JButton btnNewPlayers;
    private JButton btnSavedPlayers;
    private JButton btnDeletePlayer;
    private JButton btnReturnToMenu;
    
    
    /**
     * Constructs the PlayerRegistrationView UI of Fatal Fantasy: Tactics Game.
     */
    public PlayerRegistrationView() {

        initUI();
        


    }


    /**
     * Initializes the UI components and arranges them in the main layout.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private Image bg = new ImageIcon("view/assets/PlayersRegistrationBG.jpg").getImage();
            
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

        // Add vertical space at the top
        backgroundPanel.add(Box.createVerticalStrut(100));

        // Logo image centered and scaled
        ImageIcon logoIcon = new ImageIcon("view/assets/PlayerRegLogo.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(500, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(logoLabel);

        // Add vertical space between logo and buttons
        backgroundPanel.add(Box.createVerticalStrut(40));

        // Panel for buttons, centered
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        btnNewPlayers = new RoundedButton(NEW_PLAYERS);
        btnSavedPlayers = new RoundedButton(SAVED_PLAYERS);
        btnDeletePlayer = new RoundedButton(DELETE_PLAYER);
        btnReturnToMenu = new RoundedButton(RETURN_TO_MENU);

        // Add buttons with vertical spacing (how they stack)
        buttonPanel.add(btnNewPlayers);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnSavedPlayers);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnDeletePlayer);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnReturnToMenu);

        // Center the button panel horizontally
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(buttonPanel);

        // Add vertical glue to push everything to the center
        backgroundPanel.add(Box.createVerticalGlue());

        setLayout(new BorderLayout());
        add(backgroundPanel);
    }


    /**
     * Sets the action listener for the button click events.
     * 
     * @param listener The listener
     */
    public void setActionListener(ActionListener listener) {
        btnNewPlayers.addActionListener(listener);
        btnSavedPlayers.addActionListener(listener);
        btnReturnToMenu.addActionListener(listener);
        btnDeletePlayer.addActionListener(listener);
    }

}
