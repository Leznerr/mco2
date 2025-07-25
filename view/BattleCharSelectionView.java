package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// import controller._;

/**
 * The battle character selection view for Fatal Fantasy: Tactics Game.
 */
public class BattleCharSelectionView extends JPanel {
    private int playerID;

    // Button labels
    public static final String SELECT = "Select";
    public static final String RETURN = "Return";

    // UI components
    private JButton btnSelect;
    private JButton btnReturn;
    private JComboBox<String> charDropdown;
    private JTextArea charListArea;


    /**
     * Constructs the Battle Character Selection UI of Fatal Fantasy: Tactics Game.
     */
    public BattleCharSelectionView(int playerID) {

        this.playerID = playerID;

        initUI();
        


    }


    /**
     * Returns the title for the battle character selection view based on the Player ID.
     * @param playerID
     * @return the title string
     */
    private static String getTitleForPlayer(int playerID) {
        if (playerID == 1 || playerID == 2) {
            return "Fatal Fantasy: Tactics | Player " + playerID + " Battle Character Selection";
        } else {
            return "Fatal Fantasy: Tactics | Player Battle Character Selection";
        }
    }


    /**
     * Initializes the UI components and arranges them in the main layout.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private Image bg = new ImageIcon("view/assets/CharSelectAndBattleBG.jpg").getImage();

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
        centerPanel.add(Box.createVerticalStrut(40));

        String headlineImagePath = String.format("view/assets/Player%dBattleCharSelectionLogo.png", playerID);
        ImageIcon logoIcon = new ImageIcon(headlineImagePath);
        Image logoImg = logoIcon.getImage().getScaledInstance(500, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);

        centerPanel.add(Box.createVerticalStrut(10));

        // Rounded display box for character list
        RoundedDisplayBox detailsPanel = new RoundedDisplayBox();
        detailsPanel.setPreferredSize(new Dimension(400, 500));
        detailsPanel.setMaximumSize(new Dimension(400, 500));
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Text area for character list
        charListArea = new JTextArea();
        charListArea.setFont(new Font("Serif", Font.PLAIN, 18));
        charListArea.setForeground(Color.WHITE);
        charListArea.setOpaque(false);
        charListArea.setEditable(false);
        charListArea.setLineWrap(true);
        charListArea.setWrapStyleWord(true);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(charListArea);
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

        JLabel selectLabel = new JLabel("Select a Character:");
        selectLabel.setFont(new Font("Serif", Font.BOLD, 18));
        selectLabel.setForeground(Color.WHITE);
        selectLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        dropdownPanel.add(selectLabel);

        charDropdown = new JComboBox<>();
        charDropdown.setFont(new Font("Serif", Font.BOLD, 18));
        charDropdown.setMaximumSize(new Dimension(250, 35));
        charDropdown.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3)); 
        dropdownPanel.add(charDropdown);

        centerPanel.add(dropdownPanel);

        centerPanel.add(Box.createVerticalGlue());

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 50));
        buttonPanel.setOpaque(false);

        btnSelect = new RoundedButton(SELECT);
        btnReturn = new RoundedButton(RETURN);

        buttonPanel.add(btnSelect);
        buttonPanel.add(btnReturn);

        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(backgroundPanel);
    }


    /**
     * Sets the action listener for the button click and dropdown events.
     * 
     * @param listener The listener
     */
    public void setActionListener(ActionListener listener) {
        btnReturn.addActionListener(listener);
        btnSelect.addActionListener(listener);
        charDropdown.addActionListener(listener);
    }


    /**
     * Updates the character list display area.
     * 
     * @param text The formatted character list string
     */
    public void updateCharacterList(String text) {
        charListArea.setText(text);
    }

    
    /**
     * Confirms character selection with the user.
     * 
     * @param characterName The name of the character to be deleted
     * @return true if the user confirms deletion, false otherwise
     */
    public boolean confirmCharacterSelection(String characterName) {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to select \"" + characterName + "\"?",
            "Confirm Selection",
            JOptionPane.YES_NO_OPTION
        );
        return option == JOptionPane.YES_OPTION;
    }


    /**
     * Sets the character options in the dropdown.
     * 
     * @param characterNames The array of character names
     */
    public void setCharacterOptions(String[] characterNames) {
        charDropdown.removeAllItems();

        for (String name : characterNames) {
            charDropdown.addItem(name);
        }
    }


    /**
     * Resets all dropdown selections.
     */
    public void resetDropdowns() {
        charDropdown.setSelectedIndex(-1);
    }


    public String getSelectedCharacter() {
        return (String) charDropdown.getSelectedItem();
    }

}