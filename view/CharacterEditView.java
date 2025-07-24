package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * View class for editing an existing character in Fatal Fantasy: Tactics.
 *
 * <p>This screen allows players to select a character and customize their abilities and magic item.
 * This is a pure view — no business logic is included here.</p>
 */
public class CharacterEditView extends JFrame {

    private final int playerID;

    public static final String EDIT   = "Edit";
    public static final String RETURN = "Return";

    private final JComboBox<String> dropdownCharacter = new JComboBox<>();
    private final JComboBox<String> dropdownAbility1  = new JComboBox<>();
    private final JComboBox<String> dropdownAbility2  = new JComboBox<>();
    private final JComboBox<String> dropdownAbility3  = new JComboBox<>();
    private final JComboBox<String> dropdownMagicItem = new JComboBox<>();

    private final JButton btnEdit   = new RoundedButton(EDIT);
    private final JButton btnReturn = new RoundedButton(RETURN);

    /**
     * Constructs the CharacterEditView for a specific player.
     *
     * @param playerID the player ID (1 or 2)
     */
    public CharacterEditView(int playerID) {
        super("Fatal Fantasy: Tactics | Player " + playerID + " Character Edit");
        this.playerID = playerID;

        initUI();
        configureWindow();
    }

    private void configureWindow() {
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        CharacterEditView.this,
                        "Are you sure you want to quit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) dispose();
            }
        });

       
    }

    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private final Image bg = new ImageIcon("view/assets/CharEditBG.jpg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                double scale = Math.max(panelWidth / (double) bg.getWidth(null),
                                        panelHeight / (double) bg.getHeight(null));
                int width = (int) (bg.getWidth(null) * scale);
                int height = (int) (bg.getHeight(null) * scale);
                int x = (panelWidth - width) / 2;
                int y = (panelHeight - height) / 2;
                g.drawImage(bg, x, y, width, height, this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalStrut(40));

        String logoPath = String.format("view/assets/Player%dCharEditLogo.png", playerID);
        ImageIcon logoIcon = new ImageIcon(new ImageIcon(logoPath).getImage().getScaledInstance(550, -1, Image.SCALE_SMOOTH));
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(createDropdownPanel("Select Character:", dropdownCharacter));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createDropdownPanel("Select Ability 1:", dropdownAbility1));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createDropdownPanel("Select Ability 2:", dropdownAbility2));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createDropdownPanel("Select Ability 3:", dropdownAbility3));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createDropdownPanel("Un/Equip Magic Item:", dropdownMagicItem));
        centerPanel.add(Box.createVerticalGlue());

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 50));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnEdit);
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
        Dimension labelSize = new Dimension(200, label.getPreferredSize().height);
        label.setPreferredSize(labelSize);
        label.setMinimumSize(labelSize);
        label.setMaximumSize(labelSize);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        dropdown.setFont(new Font("Serif", Font.BOLD, 18));
        dropdown.setMaximumSize(new Dimension(250, 35));
        dropdown.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        panel.add(label);
        panel.add(dropdown);
        return panel;
    }

    public void setActionListener(ActionListener listener) {
        btnEdit.addActionListener(listener);
        btnReturn.addActionListener(listener);
        dropdownCharacter.addActionListener(listener);
        dropdownAbility1.addActionListener(listener);
        dropdownAbility2.addActionListener(listener);
        dropdownAbility3.addActionListener(listener);
        dropdownMagicItem.addActionListener(listener);
    }

    public void setCharacterOptions(String[] characterNames) {
        dropdownCharacter.removeAllItems();
        for (String name : characterNames) dropdownCharacter.addItem(name);
    }

    public void setAbilityOptions(int slot, String[] options) {
        JComboBox<String> target = switch (slot) {
            case 1 -> dropdownAbility1;
            case 2 -> dropdownAbility2;
            case 3 -> dropdownAbility3;
            default -> throw new IllegalArgumentException("Invalid slot: " + slot);
        };
        target.removeAllItems();
        for (String option : options) target.addItem(option);
    }

    public void setMagicItemOptions(String[] items) {
        dropdownMagicItem.removeAllItems();
        for (String item : items) dropdownMagicItem.addItem(item);
    }

    public void resetFields() {
        dropdownCharacter.setSelectedIndex(-1);
        dropdownAbility1.setSelectedIndex(-1);
        dropdownAbility2.setSelectedIndex(-1);
        dropdownAbility3.setSelectedIndex(-1);
        dropdownMagicItem.setSelectedIndex(-1);
    }

    public boolean confirmCharacterEdit(String name) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to edit \"" + name + "\"?",
                "Confirm Edit",
                JOptionPane.YES_NO_OPTION);
        return option == JOptionPane.YES_OPTION;
    }

    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public String getSelectedCharacter() {
        return (String) dropdownCharacter.getSelectedItem();
    }

    public String[] getSelectedAbilities() {
        return new String[]{
                (String) dropdownAbility1.getSelectedItem(),
                (String) dropdownAbility2.getSelectedItem(),
                (String) dropdownAbility3.getSelectedItem()
        };
    }

    public String getSelectedMagicItem() {
        return (String) dropdownMagicItem.getSelectedItem();
    }
}
