package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// import controller._;

/**
 * The manual character creation view for Fatal Fantasy: Tactics Game.
 */
public class CharacterManualCreationView extends JPanel {
    private int playerID;

    // Button labels
    public static final String CREATE = "Create";
    public static final String RETURN = "Return";

    // UI components
    private RoundedTextField charNameField;
    private JComboBox<String> dropdown1 = new JComboBox<>();
    private JComboBox<String> dropdown2 = new JComboBox<>();
    private JComboBox<String> dropdown3 = new JComboBox<>();
    private JComboBox<String> dropdown4 = new JComboBox<>();
    private JComboBox<String> dropdown5 = new JComboBox<>();
    private JComboBox<String> dropdown6 = new JComboBox<>();
    private JPanel ability4Panel;
    private JButton btnCreate;
    private JButton btnReturn;

    /**
     * Constructs the Manual Character Creation UI of Fatal Fantasy: Tactics Game.
     */
    public CharacterManualCreationView(int playerID) {

        this.playerID = playerID;

        initUI();
        


    }


    /**
     * Initializes the UI components and arranges them in the main layout.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private Image bg = new ImageIcon("view/assets/CharCreationBG.jpg").getImage();
            
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

        // Center panel for logo and text fields
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        centerPanel.add(Box.createVerticalStrut(40));
        String headlineImagePath = String.format("view/assets/Player%dManualCharCreationLogo.png", playerID);
        ImageIcon logoIcon = new ImageIcon(headlineImagePath);
        Image logoImg = logoIcon.getImage().getScaledInstance(550, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);
        
        centerPanel.add(Box.createVerticalStrut(10));

        // Character name input field
        charNameField = new RoundedTextField("Enter character name", 20);
        charNameField.setMaximumSize(new Dimension(300, 35));
        centerPanel.add(charNameField);
        centerPanel.add(Box.createVerticalStrut(40));

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // Dropdown Panels with Outlined Labels
        centerPanel.add(createDropdownPanel("Select Race:      ", dropdown1));
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(createDropdownPanel("Select Class:     ", dropdown2));
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(createDropdownPanel("Select Ability 1:", dropdown3));
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(createDropdownPanel("Select Ability 2:", dropdown4));
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(createDropdownPanel("Select Ability 3:", dropdown5));
        ability4Panel = createDropdownPanel("Select Ability 4:", dropdown6);
        ability4Panel.setVisible(false);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(ability4Panel);
        centerPanel.add(Box.createVerticalStrut(20));

        // Bottom panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 50));
        buttonPanel.setOpaque(false);

        btnCreate = new RoundedButton(CREATE);
        btnReturn = new RoundedButton(RETURN);

        buttonPanel.add(btnCreate);
        buttonPanel.add(btnReturn);

        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(backgroundPanel);
    }


    /**
     * Helper method to create dropdown panels with outlined labels
     * 
     * @param labelText the text for the label
     * @param dropdown the JComboBox to be added
     * @return a JPanel containing the label and dropdown
     */
    private JPanel createDropdownPanel(String labelText, JComboBox<String> dropdown) {
        JPanel dropdownPanel = new JPanel();
        dropdownPanel.setOpaque(false);
        dropdownPanel.setLayout(new BoxLayout(dropdownPanel, BoxLayout.X_AXIS));
        dropdownPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        OutlinedLabel label = new OutlinedLabel(labelText);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        // Force all labels to same preferred width
        int fixedLabelWidth = 140; // adjust as needed
        Dimension labelSize = new Dimension(fixedLabelWidth, label.getPreferredSize().height);
        label.setPreferredSize(labelSize);
        label.setMinimumSize(labelSize);
        label.setMaximumSize(labelSize);

        dropdown.setFont(new Font("Serif", Font.BOLD, 18));
        dropdown.setMaximumSize(new Dimension(250, 35));
        dropdown.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        dropdownPanel.add(label);
        dropdownPanel.add(dropdown);

        return dropdownPanel;
    }

    
    /**
     * Sets the action listener for the button click events.
     * 
     * @param listener The listener
     */
    public void setActionListener(ActionListener listener) {
        btnCreate.addActionListener(listener);
        btnReturn.addActionListener(listener);
        dropdown1.addActionListener(listener);
        dropdown2.addActionListener(listener);  
        dropdown3.addActionListener(listener);
        dropdown4.addActionListener(listener);
        dropdown5.addActionListener(listener);
        dropdown6.addActionListener(listener);
    }


    /**
     * Confirms character creation with the user.
     * 
     * @param characterName The name of the character to be created
     * @return true if the user confirms creation, false otherwise
     */
    public boolean confirmCharacterCreation(String characterName) {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to create \"" + characterName + "\"?",
            "Confirm Creation",
            JOptionPane.YES_NO_OPTION
        );

        return option == JOptionPane.YES_OPTION;
    }


    /**
     * Sets the available race options in the dropdown.
     * 
     * @param options The array of race options
     */
    public void setRaceOptions(String[] options) {
        dropdown1.removeAllItems();
        for (String opt : options) dropdown1.addItem(opt);
    }


    /**
     * Sets the available class options in the dropdown.
     * 
     * @param options The array of class options
     */
    public void setClassOptions(String[] options) {
        dropdown2.removeAllItems();
        for (String opt : options) dropdown2.addItem(opt);
    }

    /**
     * Sets the available ability options in the dropdown.
     * 
     * @param abilitySlot The slot number (1, 2, or 3)
     * @param options The array of ability options
     * 
     */
    public void setAbilityOptions(int abilitySlot, String[] options) {
        JComboBox<String> target = switch (abilitySlot) {
            case 1 -> dropdown3;
            case 2 -> dropdown4;
            case 3 -> dropdown5;
            case 4 -> dropdown6;
            default -> throw new IllegalArgumentException("Invalid ability slot: " + abilitySlot);
        };
        target.removeAllItems();

        for (String opt : options) target.addItem(opt);
    }


    /**
     * Clears the character name and resets all dropdown selections.
     */
    public void resetFields() {
        charNameField.setText("");
        dropdown1.setSelectedIndex(-1);
        dropdown2.setSelectedIndex(-1);
        dropdown3.setSelectedIndex(-1);
        dropdown4.setSelectedIndex(-1);
        dropdown5.setSelectedIndex(-1);
        dropdown6.setSelectedIndex(-1);
    }


    public String getCharacterName() {
        return charNameField.getText().trim();
    }


    public String getSelectedRace() {
        return (String) dropdown1.getSelectedItem();
    }


    public String getSelectedClass() {
        return (String) dropdown2.getSelectedItem();
    }


    public String[] getSelectedAbilities() {
        return new String[] {
            (String) dropdown3.getSelectedItem(),
            (String) dropdown4.getSelectedItem(),
            (String) dropdown5.getSelectedItem(),
            (String) dropdown6.getSelectedItem()
        };
    }

    public void setAbility4Visible(boolean visible) {
        ability4Panel.setVisible(visible);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

}