package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


import controller.CharacterManualCreationController;

/**
 * The manual character creation view for Fatal Fantasy: Tactics Game.
 * <p>
 * Pure view: renders UI, exposes listener registration and setter methods.
 * No game logic.
 */
public class CharacterManualCreationView extends JFrame {

    private final String playerName;

    // Command constants
    public static final String CREATE = "CREATE";
    public static final String RETURN = "RETURN";
    public static final String CLASS_CHANGED = "CLASS_CHANGED";

    // UI components
    private final RoundedTextField charNameField;
    private final JComboBox<String> dropdownRace = new JComboBox<>();
    private final JComboBox<String> dropdownClass = new JComboBox<>();
    private final JComboBox<String> dropdownAbility1 = new JComboBox<>();
    private final JComboBox<String> dropdownAbility2 = new JComboBox<>();
    private final JComboBox<String> dropdownAbility3 = new JComboBox<>();
    private final JButton btnCreate;
    private final JButton btnReturn;

    private CharacterManualCreationController controller;

    public CharacterManualCreationView(String playerName) {
        super("Fatal Fantasy: Tactics | Manual Character Creation - " + playerName);
        this.playerName = playerName;

        charNameField = new RoundedTextField("Enter character name", 20);
        btnCreate = new RoundedButton(CREATE);
        btnReturn = new RoundedButton(RETURN);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        buildUI();

        // Confirm dialog on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opt = JOptionPane.showConfirmDialog(
                        CharacterManualCreationView.this,
                        "Are you sure you want to quit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) dispose();
            }
        });

      
    }

    private void buildUI() {
        JPanel backgroundPanel = new JPanel() {
            private final Image bg = new ImageIcon("view/assets/CharCreationBG.jpg").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth(), h = getHeight();
                double scale = Math.max(
                        w / (double) bg.getWidth(null),
                        h / (double) bg.getHeight(null));
                int ww = (int) (bg.getWidth(null) * scale);
                int hh = (int) (bg.getHeight(null) * scale);
                g.drawImage(bg, (w - ww) / 2, (h - hh) / 2, ww, hh, this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalStrut(40));

        // Logo
        String headlineImagePath = String.format("view/assets/Player%sManualCharCreationLogo.png", playerName);
        ImageIcon logoIcon = new ImageIcon(headlineImagePath);
        Image logoImg = logoIcon.getImage().getScaledInstance(550, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);
        centerPanel.add(Box.createVerticalStrut(10));

        // Character name
        charNameField.setMaximumSize(new Dimension(300, 35));
        centerPanel.add(charNameField);
        centerPanel.add(Box.createVerticalStrut(40));

        // Dropdown panels
        centerPanel.add(createDropdownPanel("Select Race:      ", dropdownRace));
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(createDropdownPanel("Select Class:     ", dropdownClass));
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(createDropdownPanel("Select Ability 1:", dropdownAbility1));
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(createDropdownPanel("Select Ability 2:", dropdownAbility2));
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(createDropdownPanel("Select Ability 3:", dropdownAbility3));
        centerPanel.add(Box.createVerticalStrut(20));

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // Button row
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 50));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnCreate);
        buttonPanel.add(btnReturn);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(backgroundPanel);
    }

    private JPanel createDropdownPanel(String labelText, JComboBox<String> dropdown) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        OutlinedLabel label = new OutlinedLabel(labelText);
        int fixedLabelWidth = 140;
        Dimension labelSize = new Dimension(fixedLabelWidth, label.getPreferredSize().height);
        label.setPreferredSize(labelSize);
        label.setMinimumSize(labelSize);
        label.setMaximumSize(labelSize);

        dropdown.setFont(new Font("Serif", Font.BOLD, 18));
        dropdown.setMaximumSize(new Dimension(250, 35));
        dropdown.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        panel.add(label);
        panel.add(dropdown);
        return panel;
    }

    // --- Listener Registration (for Controller) ---

    public void addCreateCharacterListener(ActionListener l) {
        btnCreate.setActionCommand(CREATE);
        btnCreate.addActionListener(l);
    }

    public void addReturnListener(ActionListener l) {
        btnReturn.setActionCommand(RETURN);
        btnReturn.addActionListener(l);
    }

    public void addClassDropdownListener(ActionListener l) {
        dropdownClass.setActionCommand(CLASS_CHANGED);
        dropdownClass.addActionListener(l);
    }

    // --- Setters for Controller to populate dropdowns ---

    public void setRaceOptions(String[] races) {
        dropdownRace.removeAllItems();
        for (String r : races) dropdownRace.addItem(r);
    }

    public void setClassOptions(String[] classes) {
        dropdownClass.removeAllItems();
        for (String c : classes) dropdownClass.addItem(c);
    }

    public void setAbilityOptions(int abilitySlot, String[] abilities) {
        JComboBox<String> target = switch (abilitySlot) {
            case 1 -> dropdownAbility1;
            case 2 -> dropdownAbility2;
            case 3 -> dropdownAbility3;
            default -> throw new IllegalArgumentException("Invalid ability slot: " + abilitySlot);
        };
        target.removeAllItems();
        for (String a : abilities) target.addItem(a);
    }

    public void resetFields() {
        charNameField.setText("");
        dropdownRace.setSelectedIndex(-1);
        dropdownClass.setSelectedIndex(-1);
        dropdownAbility1.setSelectedIndex(-1);
        dropdownAbility2.setSelectedIndex(-1);
        dropdownAbility3.setSelectedIndex(-1);
    }

    // --- Getters for Controller to retrieve input ---

    public String getCharacterName() { return charNameField.getText().trim(); }

    public String getSelectedRace() { return (String) dropdownRace.getSelectedItem(); }

    public String getSelectedClass() { return (String) dropdownClass.getSelectedItem(); }

    public String[] getSelectedAbilities() {
        return new String[] {
            (String) dropdownAbility1.getSelectedItem(),
            (String) dropdownAbility2.getSelectedItem(),
            (String) dropdownAbility3.getSelectedItem()
        };
    }

    // --- Feedback dialogs ---
    public void showInfoMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean confirmCharacterCreation(String characterName) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to create \"" + characterName + "\"?",
                "Confirm Creation",
                JOptionPane.YES_NO_OPTION
        );
        return option == JOptionPane.YES_OPTION;
    }

    // Optional: expose dropdowns for advanced use
    public JComboBox<String> getRaceDropdown()   { return dropdownRace; }
    public JComboBox<String> getClassDropdown()  { return dropdownClass; }
    public JComboBox<String> getAbility1Dropdown() { return dropdownAbility1; }
    public JComboBox<String> getAbility2Dropdown() { return dropdownAbility2; }
    public JComboBox<String> getAbility3Dropdown() { return dropdownAbility3; }

    // Controller setter (optional, for reference by controller)
    public void setController(CharacterManualCreationController controller) {
        this.controller = controller;
    }
}
