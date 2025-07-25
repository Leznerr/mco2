package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// import controller._;

/**
 * The players deletion view for Fatal Fantasy: Tactics Game.
 */
public class PlayerDeleteView extends JPanel {

    // Button labels
    public static final String DELETE = "Delete";
    public static final String RETURN = "Return";

    // UI components
    private JButton btnDelete;
    private JButton btnReturn;
    private JComboBox<String> playerDropdown;
    private JTextArea playerListArea;


    /**
     * Constructs the Specific Player Deletion UI of Fatal Fantasy: Tactics Game.
     */
    public PlayerDeleteView() {

        initUI();
        


    }


    /**
     * Initializes the UI components and arranges them in the main layout.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private Image bg = new ImageIcon("view/assets/DeleteCharBG.jpg").getImage();

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

        backgroundPanel.setLayout(new BorderLayout());

        // Center panel for headline + display box
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalStrut(30));

        ImageIcon logoIcon = new ImageIcon("view/assets/DeletePlayerLogo.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(480, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);

        centerPanel.add(Box.createVerticalStrut(10));

        // Rounded display box for player list
        RoundedDisplayBox detailsPanel = new RoundedDisplayBox();
        detailsPanel.setPreferredSize(new Dimension(400, 500));
        detailsPanel.setMaximumSize(new Dimension(400, 500));
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Text area for player list
        playerListArea = new JTextArea();
        playerListArea.setFont(new Font("Serif", Font.PLAIN, 18));
        playerListArea.setForeground(Color.WHITE);
        playerListArea.setOpaque(false);
        playerListArea.setEditable(false);
        playerListArea.setLineWrap(true);
        playerListArea.setWrapStyleWord(true);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(playerListArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        detailsPanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(detailsPanel);
        centerPanel.add(Box.createVerticalStrut(40));

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // Horizontal panel for label and dropdown
        JPanel dropdownPanel = new JPanel();
        dropdownPanel.setOpaque(false);
        dropdownPanel.setLayout(new BoxLayout(dropdownPanel, BoxLayout.X_AXIS));
        dropdownPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel selectLabel = new JLabel("Select a Player:");
        selectLabel.setFont(new Font("Serif", Font.BOLD, 18));
        selectLabel.setForeground(Color.WHITE);
        selectLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        dropdownPanel.add(selectLabel);

        playerDropdown = new JComboBox<>();
        playerDropdown.setFont(new Font("Serif", Font.BOLD, 18));
        playerDropdown.setMaximumSize(new Dimension(250, 35));
        playerDropdown.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3)); 
        dropdownPanel.add(playerDropdown);

        centerPanel.add(dropdownPanel);

        centerPanel.add(Box.createVerticalGlue());

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 50));
        buttonPanel.setOpaque(false);

        btnDelete = new RoundedButton(DELETE);
        btnReturn = new RoundedButton(RETURN);

        buttonPanel.add(btnDelete);
        buttonPanel.add(btnReturn);

        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(backgroundPanel);
    }


    /**
     * Sets the action listener for the button click events.
     * 
     * @param listener The listener
     */
    public void setActionListener(ActionListener listener) {
        btnReturn.addActionListener(listener);
        btnDelete.addActionListener(listener);
        playerDropdown.addActionListener(listener);
    }


    /**
     * Updates the player list display area.
     * 
     * @param text The formatted player list string
     */
    public void updatePlayerList(String text) {
        playerListArea.setText(text);
    }

    
    /**
     * Confirms player deletion with the user.
     * 
     * @param playerName The name of the player to be deleted
     * @return true if the user confirms deletion, false otherwise
     */
    public boolean confirmPlayerDeletion(String playerName) {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete \"" + playerName + "\"?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION
        );
        return option == JOptionPane.YES_OPTION;
    }


    /**
     * Sets the player options in the dropdown.
     * 
     * @param playerNames The array of player names
     */
    public void setPlayerOptions(String[] playerNames) {
        playerDropdown.removeAllItems();

        for (String name : playerNames) {
            playerDropdown.addItem(name);
        }
    }


    /**
     * Resets all dropdown selections.
     */
    public void resetDropdowns() {
        playerDropdown.setSelectedIndex(-1);
    }


    public String getSelectedPlayer() {
        return (String) playerDropdown.getSelectedItem();
    }

}
