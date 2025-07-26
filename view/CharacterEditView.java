package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// import controller._;

/**
 * The character edit view for Fatal Fantasy: Tactics Game.
 */
public class CharacterEditView extends JFrame {
private int playerID;

    // Button labels
    public static final String EDIT = "Edit";
    public static final String RETURN = "Return";

    // UI components
    private JComboBox<String> dropdown1 = new JComboBox<>();
    private JComboBox<String> dropdown2 = new JComboBox<>();
    private JComboBox<String> dropdown3 = new JComboBox<>();
    private JComboBox<String> dropdown4 = new JComboBox<>();
    private JComboBox<String> dropdown5 = new JComboBox<>();
    private JButton btnEdit;
    private JButton btnReturn;

    /**
     * Constructs the Manual Character Creation UI of Fatal Fantasy: Tactics Game.
     */
    public CharacterEditView(int playerID) {
        super("Fatal Fantasy: Tactics | Player " + playerID + " Character Edit");

        this.playerID = playerID;

        initUI();
        
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    CharacterEditView.this,
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
        setVisible(true);
    }


    /**
     * Initializes the UI components and arranges them in the main layout.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private Image bg = new ImageIcon("view/assets/CharEditBG.jpg").getImage();
            
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
        String headlineImagePath = String.format("view/assets/Player%dCharEditLogo.png", playerID);
        ImageIcon logoIcon = new ImageIcon(headlineImagePath);
        Image logoImg = logoIcon.getImage().getScaledInstance(550, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);
        
        centerPanel.add(Box.createVerticalStrut(10));

        // Dropdown Panels with Outlined Labels
        centerPanel.add(createDropdownPanel("Select Character:", dropdown1));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createDropdownPanel("Select Ability 1:", dropdown2));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createDropdownPanel("Select Ability 2:", dropdown3));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createDropdownPanel("Select Ability 3:", dropdown4));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createDropdownPanel("Un/Equip Magic Item:", dropdown5));
        centerPanel.add(Box.createVerticalStrut(20));

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 50));
        buttonPanel.setOpaque(false);

        btnEdit = new RoundedButton(EDIT);
        btnReturn = new RoundedButton(RETURN);

        buttonPanel.add(btnEdit);
        buttonPanel.add(btnReturn);

        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(backgroundPanel);
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

        // Force fixed label width
        int fixedWidth = 200; // Choose width equal to longest label
        Dimension labelSize = new Dimension(fixedWidth, label.getPreferredSize().height);
        label.setPreferredSize(labelSize);
        label.setMinimumSize(labelSize);
        label.setMaximumSize(labelSize);

        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

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
        btnEdit.addActionListener(listener);
        btnReturn.addActionListener(listener);
        dropdown1.addActionListener(listener);
        dropdown2.addActionListener(listener);  
        dropdown3.addActionListener(listener);
        dropdown4.addActionListener(listener);
        dropdown5.addActionListener(listener);
    }


    /**
     * Confirms character creation with the user.
     * 
     * @param characterName The name of the character to be created
     * @return true if the user confirms creation, false otherwise
     */
    public boolean confirmCharacterEdit(String characterName) {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to edit \"" + characterName + "\"?",
            "Confirm Edit",
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
        dropdown1.removeAllItems();

        for (String name : characterNames) {
            dropdown1.addItem(name);
        }
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
            case 1 -> dropdown2;
            case 2 -> dropdown3;
            case 3 -> dropdown4;
            default -> throw new IllegalArgumentException("Invalid ability slot: " + abilitySlot);
        };
        target.removeAllItems();

        for (String opt : options) target.addItem(opt);
    }


    /**
     * Sets the available magic item options in the dropdown.
     * 
     * @param items The array of magic item options
     */
    public void setMagicItemOptions(String[] items) {
        dropdown5.removeAllItems();
        for (String item : items) {
            dropdown5.addItem(item);
        }
    }


    /**
     * Clears the character name and resets all dropdown selections.
     */
    public void resetFields() {
        dropdown1.setSelectedIndex(-1);
        dropdown2.setSelectedIndex(-1);
        dropdown3.setSelectedIndex(-1);
        dropdown4.setSelectedIndex(-1);
        dropdown5.setSelectedIndex(-1);
    }


    public String getSelectedCharacter() {
        return (String) dropdown1.getSelectedItem();
    }


    public String[] getSelectedAbilities() {
        return new String[] {
            (String) dropdown2.getSelectedItem(),
            (String) dropdown3.getSelectedItem(),
            (String) dropdown4.getSelectedItem()
        };
    }


    public String getMagicItem() {
        return (String) dropdown5.getSelectedItem();
    }

}