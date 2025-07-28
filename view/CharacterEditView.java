package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.List;

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
    // Ability dropdowns are created dynamically
    private final java.util.List<JComboBox<String>> abilityDropdowns = new ArrayList<>();
    private final JComboBox<String> dropdownMagicItem = new JComboBox<>();

    private JPanel abilitiesPanel;
    private Component abilitiesToItemSpacer;
    private final List<JPanel> abilityPanels = new ArrayList<>();
    private int abilityCount = 3;

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

        abilitiesPanel = new JPanel();
        abilitiesPanel.setOpaque(false);
        abilitiesPanel.setLayout(new BoxLayout(abilitiesPanel, BoxLayout.Y_AXIS));

        // initialise default dropdowns
        setAbilityCount(abilityCount);

        centerPanel.add(abilitiesPanel);

        abilitiesToItemSpacer = Box.createVerticalStrut(20);
        centerPanel.add(abilitiesToItemSpacer);

        centerPanel.add(createDropdownPanel("Un/Equip Magic Item (includes N/A)", dropdownMagicItem));
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
        JPanel dropdownPanel = new JPanel();
        dropdownPanel.setOpaque(false);
        dropdownPanel.setLayout(new BoxLayout(dropdownPanel, BoxLayout.X_AXIS));
        dropdownPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        OutlinedLabel label = new OutlinedLabel(labelText);

        int fixedWidth = 200;
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

    public void setActionListener(ActionListener listener) {
        btnEdit.addActionListener(listener);
        btnReturn.addActionListener(listener);
        dropdownCharacter.addActionListener(listener);
        for (JComboBox<String> dd : abilityDropdowns) {
            dd.addActionListener(listener);
        }
        dropdownMagicItem.addActionListener(listener);
    }

    public void setCharacterOptions(String[] characterNames) {
        dropdownCharacter.removeAllItems();
        for (String name : characterNames) dropdownCharacter.addItem(name);
    }

    public void setAbilityOptions(int slot, String[] options) {
        if (slot < 1 || slot > abilityDropdowns.size()) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        }
        JComboBox<String> target = abilityDropdowns.get(slot - 1);
        target.removeAllItems();
        for (String option : options) {
            target.addItem(option);
        }
    }

    public void setMagicItemOptions(String[] items) {
        dropdownMagicItem.removeAllItems();
        for (String item : items) dropdownMagicItem.addItem(item);
    }

    public void setSelectedAbility(int slot, String abilityName) {
        if (slot < 1 || slot > abilityDropdowns.size()) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        }
        abilityDropdowns.get(slot - 1).setSelectedItem(abilityName);
    }

    public void setSelectedMagicItem(String itemName) {
        dropdownMagicItem.setSelectedItem(itemName);
    }

    public JComboBox<String> getCharacterDropdown() { return dropdownCharacter; }
    public JComboBox<String> getMagicItemDropdown() { return dropdownMagicItem; }

    public void resetFields() {
        dropdownCharacter.setSelectedIndex(-1);
        for (JComboBox<String> dd : abilityDropdowns) {
            dd.setSelectedIndex(-1);
        }
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
        String[] selected = new String[abilityCount];
        for (int i = 0; i < abilityCount; i++) {
            selected[i] = (String) abilityDropdowns.get(i).getSelectedItem();
        }
        return selected;
    }

    public String getSelectedAbility(int slot) {
        if (slot < 1 || slot > abilityDropdowns.size()) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        }
        return (String) abilityDropdowns.get(slot - 1).getSelectedItem();
    }

    public String getSelectedMagicItem() {
        return (String) dropdownMagicItem.getSelectedItem();
    }

    /**
     * Adjusts the number of ability dropdowns shown.
     */
    public void setAbilityCount(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Ability count must be positive");
        }
        abilityCount = count;

        while (abilityDropdowns.size() < count) {
            int idx = abilityDropdowns.size();
            JComboBox<String> dd = new JComboBox<>();
            abilityDropdowns.add(dd);
            abilityPanels.add(createDropdownPanel("Select Ability " + (idx + 1), dd));
        }

        abilitiesPanel.removeAll();
        abilitiesPanel.add(Box.createVerticalStrut(20));
        for (int i = 0; i < abilityCount; i++) {
            if (i > 0) abilitiesPanel.add(Box.createVerticalStrut(10));
            abilitiesPanel.add(abilityPanels.get(i));
        }
        abilitiesPanel.revalidate();
        abilitiesPanel.repaint();
    }

    /** Returns current number of ability dropdowns. */
    public int getAbilityCount() {
        return abilityCount;
    }
}
