package view;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.battle.CombatLog;
import model.core.Character;
import view.OutlinedLabel;

// import controller._;

/**
 * The battle view for Fatal Fantasy: Tactics Game.
 * Both player panels are always created so controllers can
 * update Player 1 and Player 2 fields regardless of battle mode.
 */
public class BattleView extends JFrame {
    public static final int BATTLE_PVP = 1;
    public static final int BATTLE_PVB = 2;

    private Character char1;
    private Character char2;

    private int mode;

    // Button labels
    public static final String P1_USE = "Use Ability/Item";
    public static final String P2_USE = "Use Ability/Item";
    public static final String REMATCH = "Rematch";
    public static final String RETURN = "Return";

    // UI components
    private JButton btnP1Use, btnP2Use, btnRematch, btnReturn;
    private JComboBox<String> cmbP1Abilities = new JComboBox<>();
    private JComboBox<String> cmbP2Abilities = new JComboBox<>();
    private JTextArea p1NameCharNameArea, p2NameCharNameArea, p1StatusArea, p2StatusArea;
    private JTextArea p1AbilitiesItemsArea, p2AbilitiesItemsArea, battleLogArea, battleOutcomeArea;
    private OutlinedLabel roundLabel;
    private int lastLogIndex = 0;
    
    /**
     * Constructs the Battle UI of Fatal Fantasy: Tactics Game.
     */
    public BattleView(int mode) {
        super(getTitleForPlayer(mode));

        this.mode = mode;

        initUI();
        
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    BattleView.this,
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
     * Convenience constructor using two characters. Defaults to PvP mode.
     */
    public BattleView(Character c1, Character c2) {
        this(BATTLE_PVP, c1, c2);
    }

    /**
     * Constructs a battle view for the given mode and characters.
     */
    public BattleView(int mode, Character c1, Character c2) {
        this(mode);
        this.char1 = c1;
        this.char2 = c2;
    }


    /**
     * Returns the title for the battle view based on the Player ID.
     * @param mode
     * @return the title string
     */
    private static String getTitleForPlayer(int mode) {
        if (mode == BATTLE_PVP) {
            return "Fatal Fantasy: Tactics | Player vs Player Battle";
        } else {
            return "Fatal Fantasy: Tactics | Player vs Bot Battle";
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
                int y = (panelHeight - height) / 2 + 80;
                g.drawImage(bg, x, y, width, height, this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        leftPanel.setOpaque(false);
        rightPanel.setOpaque(false);
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);

        centerPanel.add(Box.createVerticalStrut(40));
        roundLabel = new OutlinedLabel("Round 1");
        roundLabel.setFont(new Font("Serif", Font.BOLD, 22));
        roundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(roundLabel);
        centerPanel.add(Box.createVerticalStrut(40));

        // Rounded display box for battle log
        RoundedDisplayBox battleLogPanel = new RoundedDisplayBox();
        battleLogPanel.setPreferredSize(new Dimension(400, 380));
        battleLogPanel.setMaximumSize(new Dimension(400, 380));
        battleLogPanel.setLayout(new BorderLayout());
        battleLogPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Text area for battle log
        battleLogArea = new JTextArea();
        battleLogArea.setFont(new Font("Serif", Font.PLAIN, 18));
        battleLogArea.setForeground(Color.WHITE);
        battleLogArea.setOpaque(false);
        battleLogArea.setEditable(false);
        battleLogArea.setLineWrap(true);
        battleLogArea.setWrapStyleWord(true);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(battleLogArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        battleLogPanel.add(scrollPane, BorderLayout.CENTER);

        // Rounded display box for battle outcome
        RoundedDisplayBox battleOutcomePanel = new RoundedDisplayBox();
        battleOutcomePanel.setPreferredSize(new Dimension(400, 80));
        battleOutcomePanel.setMaximumSize(new Dimension(400, 80));
        battleOutcomePanel.setLayout(new BorderLayout());
        battleOutcomePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Text area for battle outcome
        battleOutcomeArea = new JTextArea();
        battleOutcomeArea.setFont(new Font("Serif", Font.PLAIN, 18));
        battleOutcomeArea.setForeground(Color.WHITE);
        battleOutcomeArea.setOpaque(false);
        battleOutcomeArea.setEditable(false);
        battleOutcomeArea.setLineWrap(true);
        battleOutcomeArea.setWrapStyleWord(true);
        battleOutcomePanel.add(battleOutcomeArea, BorderLayout.CENTER);

        centerPanel.add(battleLogPanel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(battleOutcomePanel);
        centerPanel.add(Box.createVerticalStrut(10));

        // Bottom Panel (buttons)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 50));
        buttonPanel.setOpaque(false);

        btnRematch = new RoundedButton(REMATCH);
        btnReturn = new RoundedButton(RETURN);
        btnRematch.setEnabled(false);
        btnReturn.setEnabled(false);

        buttonPanel.add(btnRematch);
        buttonPanel.add(btnReturn);

        // Left & Right Panels
        setupPlayerPanel(leftPanel, 1);
        setupPlayerPanel(rightPanel, 2);

        backgroundPanel.add(leftPanel, BorderLayout.WEST);
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        backgroundPanel.add(rightPanel, BorderLayout.EAST);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);


        setContentPane(backgroundPanel);
    }

    /**
     * Helper method to set up the player panel with its components.
     * 
     * @param panel the panel to set up
     * @param playerID the ID of the player
     */
    private void setupPlayerPanel(JPanel panel, int playerID) {
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Dimension fixedPanelSize = new Dimension(320, 700);
        panel.setPreferredSize(fixedPanelSize);
        panel.setMinimumSize(fixedPanelSize);
        panel.setMaximumSize(fixedPanelSize);

        panel.add(Box.createVerticalStrut(20));

        // Logo
        String headlineImagePath;
        if (mode == BATTLE_PVB && playerID == 2) {
            headlineImagePath = "view/assets/BotBattleLogo.png";
        } else {
            headlineImagePath = String.format("view/assets/Player%dBattleLogo.png", playerID);
        }
        ImageIcon logoIcon = new ImageIcon(headlineImagePath);
        Image logoImg = logoIcon.getImage().getScaledInstance(180, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(logoLabel);

        panel.add(Box.createVerticalStrut(10));

        // Name/CharName Area
        RoundedDisplayBox nameCharNamePanel = new RoundedDisplayBox();
        nameCharNamePanel.setPreferredSize(new Dimension(280, 40));
        nameCharNamePanel.setMaximumSize(new Dimension(280, 40));
        nameCharNamePanel.setMinimumSize(new Dimension(280, 40));
        nameCharNamePanel.setLayout(new BorderLayout());
        nameCharNamePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea nameCharNameArea;
        if (playerID == 1) {
            if (p1NameCharNameArea == null) {
                p1NameCharNameArea = new JTextArea();
            }
            nameCharNameArea = p1NameCharNameArea;
        } else {
            if (p2NameCharNameArea == null) {
                p2NameCharNameArea = new JTextArea();
            }
            nameCharNameArea = p2NameCharNameArea;
        }
        nameCharNameArea.setFont(new Font("Serif", Font.PLAIN, 18));
        nameCharNameArea.setForeground(Color.WHITE);
        nameCharNameArea.setOpaque(false);
        nameCharNameArea.setEditable(false);
        nameCharNameArea.setLineWrap(true);
        nameCharNameArea.setWrapStyleWord(true);
        nameCharNamePanel.add(nameCharNameArea, BorderLayout.CENTER);

        // Status Area
        RoundedDisplayBox statusPanel = new RoundedDisplayBox();
        statusPanel.setPreferredSize(new Dimension(280, 40));
        statusPanel.setMaximumSize(new Dimension(280, 40));
        statusPanel.setMinimumSize(new Dimension(280, 40));
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea statusArea;
        if (playerID == 1) {
            if (p1StatusArea == null) {
                p1StatusArea = new JTextArea();
            }
            statusArea = p1StatusArea;
        } else {
            if (p2StatusArea == null) {
                p2StatusArea = new JTextArea();
            }
            statusArea = p2StatusArea;
        }
        statusArea.setFont(new Font("Serif", Font.PLAIN, 18));
        statusArea.setForeground(Color.WHITE);
        statusArea.setOpaque(false);
        statusArea.setEditable(false);
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        statusPanel.add(statusArea, BorderLayout.CENTER);

        panel.add(nameCharNamePanel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(statusPanel);

        // Abilities/Items Area
        RoundedDisplayBox abilitiesItemsPanel = new RoundedDisplayBox();
        abilitiesItemsPanel.setPreferredSize(new Dimension(280, 200));
        abilitiesItemsPanel.setMaximumSize(new Dimension(280, 200));
        abilitiesItemsPanel.setMinimumSize(new Dimension(280, 200));
        abilitiesItemsPanel.setLayout(new BorderLayout());
        abilitiesItemsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea abilitiesItemsArea;
        if (playerID == 1) {
            if (p1AbilitiesItemsArea == null) {
                p1AbilitiesItemsArea = new JTextArea();
            }
            abilitiesItemsArea = p1AbilitiesItemsArea;
        } else {
            if (p2AbilitiesItemsArea == null) {
                p2AbilitiesItemsArea = new JTextArea();
            }
            abilitiesItemsArea = p2AbilitiesItemsArea;
        }
        abilitiesItemsArea.setFont(new Font("Serif", Font.PLAIN, 18));
        abilitiesItemsArea.setForeground(Color.WHITE);
        abilitiesItemsArea.setOpaque(false);
        abilitiesItemsArea.setEditable(false);
        abilitiesItemsArea.setLineWrap(true);
        abilitiesItemsArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(abilitiesItemsArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        abilitiesItemsPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(Box.createVerticalStrut(10));
        panel.add(abilitiesItemsPanel);

        // Dropdown and Use Button
        panel.add(Box.createVerticalStrut(10));
        JComboBox<String> cmbAbilities;
        JButton btnUse;
        if (playerID == 1) {
            cmbAbilities = cmbP1Abilities;
            if (btnP1Use == null) {
                btnP1Use = new RoundedButton(P1_USE);
            }
            btnUse = btnP1Use;
        } else {
            cmbAbilities = cmbP2Abilities;
            if (btnP2Use == null) {
                btnP2Use = new RoundedButton(P2_USE);
            }
            btnUse = btnP2Use;
        }
        panel.add(createDropdownPanel("Select ability/magic item to use:", cmbAbilities));

        panel.add(Box.createVerticalStrut(5));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnUse);
        panel.add(buttonPanel);

        panel.add(Box.createVerticalGlue());
    }
    /**
     * Helper method to create dropdown panels with outlined labels
     *
     * @param labelText the text for the label
     * @param dropdown the JComboBox to be added
     * @return a JPanel containing the label and dropdown
     */
    private JPanel createDropdownPanel(String labelText, JComboBox<String> dropdown) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        OutlinedLabel label = new OutlinedLabel(labelText);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Serif", Font.BOLD, 17));

        dropdown.setFont(new Font("Serif", Font.BOLD, 18));
        dropdown.setMaximumSize(new Dimension(250, 35));
        dropdown.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(dropdown);

        return panel;
    }


    /**
     * Sets the action listener for the button click and dropdown events.
     * 
     * @param listener The listener
     */
    public void setActionListener(ActionListener listener) {
        // Use Buttons
        btnP1Use.addActionListener(listener);
        btnP2Use.addActionListener(listener);

        // Dropdowns
        cmbP1Abilities.addActionListener(listener);
        cmbP2Abilities.addActionListener(listener);

        // Bottom Panel Buttons
        btnRematch.addActionListener(listener);
        btnReturn.addActionListener(listener);
    }


    /**
     * Sets the ability options in the dropdown.
     * 
     * @param playerID the ID of the player
     * @param options the list of ability options to set
     */
    public void updateAbilityDropdown(int playerID, java.util.List<String> options) {
        JComboBox<String> combo;

        if (playerID == 1) {
            combo = cmbP1Abilities;
        } else {
            combo = cmbP2Abilities;
        }

        combo.removeAllItems();

        for (String option : options) {
            combo.addItem(option);
        }
        combo.setMaximumRowCount(options.size());
    }


    /**
     * Sets the player name and character name
     * 
     * @param playerID the ID of the player
     * @param text the text to set
     */
    public void setPlayerNameAndCharName(int playerID, String text) {
        switch (playerID) {
            case 1 -> p1NameCharNameArea.setText(text);
            case 2 -> p2NameCharNameArea.setText(text);
        }
    }


    /**
     * Sets the player status
     * 
     * @param playerID the ID of the player
     * @param status the status text to set 
     */
    public void setPlayerStatus(int playerID, String status) {
        switch (playerID) {
            case 1 -> p1StatusArea.setText(status);
            case 2 -> p2StatusArea.setText(status);
        }
    }


    /**
     * Sets the abilities and items for the players' character
     * 
     * @param playerID the ID of the player
     * @param abilitiesItems the abilities and items text
     */
    public void setPlayerAbilitiesItems(int playerID, String abilitiesItems) {
        switch (playerID) {
            case 1 -> p1AbilitiesItemsArea.setText(abilitiesItems);
            case 2 -> p2AbilitiesItemsArea.setText(abilitiesItems);
        }
    }



    /**
     * Appends text to the battle log
     * 
     * @param text the text to append
     */
    public void appendBattleLog(String text) {
        battleLogArea.append(text + "\n");
    }

    /** Clears the battle log display and resets the tracking index. */
    public void resetBattleLog() {
        battleLogArea.setText("");
        lastLogIndex = 0;
    }


    /**
     * Sets the battle outcome
     * 
     * @param text the text to set
     */
    public void setBattleOutcome(String text) {
        battleOutcomeArea.setText(text);
    }

    /** Clears any text from the battle outcome area. */
    public void clearBattleOutcome() {
        battleOutcomeArea.setText("");
    }

    /**
     * Updates the displayed round number.
     *
     * @param round current battle round
     */
    public void setRoundNumber(int round) {
        if (roundLabel != null) {
            roundLabel.setText("Round " + round);
        }
    }
    

    public String getSelectedAbility(int playerID) {
        String selectedAbility;

        if (playerID == 1) {
            selectedAbility = (String) cmbP1Abilities.getSelectedItem();
        } else {
            selectedAbility = (String) cmbP2Abilities.getSelectedItem();
        }

        return selectedAbility;
    }

    // --- Additional helpers used by controllers ---

    public JComboBox<String> getAbilitySelectorP1() {
        return cmbP1Abilities;
    }

    public JComboBox<String> getAbilitySelectorP2() {
        return cmbP2Abilities;
    }

    public void addUseAbilityP1Listener(ActionListener l) {
        if (btnP1Use != null) {
            btnP1Use.addActionListener(l);
        } else {
            throw new IllegalStateException("btnP1Use not initialized");
        }
    }

    public void addUseAbilityP2Listener(ActionListener l) {
        if (btnP2Use != null) {
            btnP2Use.addActionListener(l);
        }
    }

    public void addRematchListener(ActionListener l) {
        btnRematch.addActionListener(l);
    }

    public void addReturnListener(ActionListener l) {
        btnReturn.addActionListener(l);
    }

    public void setPlayer2ControlsEnabled(boolean enabled) {
        cmbP2Abilities.setEnabled(enabled);
        if (btnP2Use != null) {
            btnP2Use.setEnabled(enabled);
        }
    }

    /** Enables or disables all ability controls for both players. */
    public void setBattleControlsEnabled(boolean enabled) {
        cmbP1Abilities.setEnabled(enabled);
        cmbP2Abilities.setEnabled(enabled);
        if (btnP1Use != null) btnP1Use.setEnabled(enabled);
        if (btnP2Use != null) btnP2Use.setEnabled(enabled);
    }

    /** Enables or disables the rematch and return buttons. */
    public void setEndButtonsEnabled(boolean enabled) {
        btnRematch.setEnabled(enabled);
        btnReturn.setEnabled(enabled);
    }

    // --- Minimal callbacks expected by BattleController ---

    public void displayBattleStart(Character c1, Character c2) {
        // The battle log already records the start message. Simply reset the
        // UI log so displayTurnResults can show entries sequentially.
        resetBattleLog();
        clearBattleOutcome();
    }

    public void displayTurnResults(CombatLog log) {
        var entries = log.getLogEntries();
        for (int i = lastLogIndex; i < entries.size(); i++) {
            appendBattleLog(entries.get(i));
        }
        lastLogIndex = entries.size();
    }

    public void displayBattleEnd(Character winner) {
        setBattleOutcome(winner.getName() + " wins!");
        setBattleControlsEnabled(false);
        setEndButtonsEnabled(true);
    }

}