package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// import controller._;

/**
 * The player character management view for Fatal Fantasy: Tactics Game.
 */
public class PlayerCharManagementView extends JPanel {
    private int playerID;

    // Button labels
    public static final String VIEW_CHAR = "View Characters";
    public static final String CREATE_CHAR = "Create Character";
    public static final String EDIT_CHAR = "Edit Character";
    public static final String DELETE_CHAR = "Delete Character";
    public static final String INVENTORY = "Inventory";
    public static final String RETURN = "Return";

    // UI components
    private JButton btnViewChar;
    private JButton btnCreateChar;
    private JButton btnEditChar;
    private JButton btnDeleteChar;
    private JButton btnInventory;
    private JButton btnReturn;

    /**
     * Constructs the Player Character Management UI of Fatal Fantasy: Tactics Game.
     */
    public PlayerCharManagementView(int playerID) {

        this.playerID = playerID;

        initUI();
        


    }


    /**
     * Initializes the UI components and arranges them in the main layout.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private Image bg = new ImageIcon("view/assets/CharAndPlayerCharManagBG.jpg").getImage();

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
        backgroundPanel.add(Box.createVerticalStrut(50));

        // Logo image centered and scaled
        String headlineImagePath = String.format("view/assets/Player%dCharManagLogo.png", playerID);
        ImageIcon logoIcon = new ImageIcon(headlineImagePath);
        Image logoImg = logoIcon.getImage().getScaledInstance(500, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(logoLabel);

        // Add vertical space between logo and buttons
        backgroundPanel.add(Box.createVerticalStrut(10));

        // Panel for buttons, centered
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        btnViewChar = new RoundedButton(VIEW_CHAR);
        btnCreateChar = new RoundedButton(CREATE_CHAR);
        btnEditChar = new RoundedButton(EDIT_CHAR);
        btnDeleteChar = new RoundedButton(DELETE_CHAR);
        btnInventory = new RoundedButton(INVENTORY);
        btnReturn = new RoundedButton(RETURN);

        // Add buttons with vertical spacing (how they stack)
        buttonPanel.add(btnViewChar);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnCreateChar);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnEditChar);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnDeleteChar);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnInventory);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnReturn);

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
        btnViewChar.addActionListener(listener);
        btnCreateChar.addActionListener(listener);
        btnEditChar.addActionListener(listener);
        btnDeleteChar.addActionListener(listener);
        btnInventory.addActionListener(listener);
        btnReturn.addActionListener(listener);
    }

}