package view;

import model.core.Character;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Battle View for Two-Character Mode in Fatal Fantasy: Tactics.
 * <p>Renders character panels, battle log, ability selectors, and controls.
 * All game logic handled via BattleController.</p>
 */
public class BattleView extends JPanel {

    // Buttons
    private JButton btnUseAbilityP1;
    private JButton btnUseAbilityP2;
    private JButton btnRematch;
    private JButton btnReturn;

    // Dropdown selectors
    private JComboBox<String> abilitySelectorP1;
    private JComboBox<String> abilitySelectorP2;

    // Display components
    private JTextArea battleLogArea;
    private JTextArea abilitiesListP1;
    private JTextArea abilitiesListP2;
    private JLabel lblPlayer1Name;
    private JLabel lblPlayer2Name;
    private JLabel lblPlayer1Stats;
    private JLabel lblPlayer2Stats;

    // Characters (Model)
    private final Character character1;
    private final Character character2;

    /**
     * Constructs the BattleView using two characters.
     * @param character1 Character controlled by Player 1
     * @param character2 Character controlled by Player 2 (or bot)
     */
    public BattleView(Character character1, Character character2) {
        this.character1 = character1;
        this.character2 = character2;

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        initializeComponents();
        renderInitialState();
    }

    /**
     * Initializes static GUI components.
     */
    private void initializeComponents() {
        JPanel backgroundPanel = new JPanel() {
            private final Image bg = new ImageIcon("view/assets/BattleArenaBG.jpg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null);

        // Player 1 GUI
        lblPlayer1Name = createLabel("", 30, 30, 200, 30);
        backgroundPanel.add(lblPlayer1Name);

        lblPlayer1Stats = createLabel("", 30, 60, 250, 30);
        backgroundPanel.add(lblPlayer1Stats);

        abilitiesListP1 = createTextArea(30, 100, 300, 200);
        backgroundPanel.add(abilitiesListP1);

        abilitySelectorP1 = new JComboBox<>();
        abilitySelectorP1.setBounds(30, 320, 250, 30);
        backgroundPanel.add(abilitySelectorP1);

        btnUseAbilityP1 = new JButton("Use Ability");
        btnUseAbilityP1.setBounds(30, 360, 150, 30);
        backgroundPanel.add(btnUseAbilityP1);

        // Player 2 GUI
        lblPlayer2Name = createLabel("", 760, 30, 200, 30);
        backgroundPanel.add(lblPlayer2Name);

        lblPlayer2Stats = createLabel("", 730, 60, 250, 30);
        backgroundPanel.add(lblPlayer2Stats);

        abilitiesListP2 = createTextArea(670, 100, 300, 200);
        backgroundPanel.add(abilitiesListP2);

        abilitySelectorP2 = new JComboBox<>();
        abilitySelectorP2.setBounds(720, 320, 250, 30);
        backgroundPanel.add(abilitySelectorP2);

        btnUseAbilityP2 = new JButton("Use Ability");
        btnUseAbilityP2.setBounds(820, 360, 150, 30);
        backgroundPanel.add(btnUseAbilityP2);

        // Battle Log
        battleLogArea = new JTextArea();
        battleLogArea.setEditable(false);
        battleLogArea.setOpaque(false);
        battleLogArea.setForeground(Color.WHITE);
        battleLogArea.setLineWrap(true);
        battleLogArea.setWrapStyleWord(true);

        JScrollPane battleLogScroll = new JScrollPane(battleLogArea);
        battleLogScroll.setBounds(360, 100, 300, 250);
        battleLogScroll.setOpaque(false);
        battleLogScroll.getViewport().setOpaque(false);
        backgroundPanel.add(battleLogScroll);

        // Control Buttons
        btnRematch = new JButton("Rematch");
        btnRematch.setBounds(420, 380, 100, 30);
        backgroundPanel.add(btnRematch);

        btnReturn = new JButton("Return");
        btnReturn.setBounds(530, 380, 100, 30);
        backgroundPanel.add(btnReturn);

        add(backgroundPanel, BorderLayout.CENTER);
    }

    /**
     * Renders the characters' starting stats and abilities.
     */
    private void renderInitialState() {
        lblPlayer1Name.setText(character1.getName());
        lblPlayer2Name.setText(character2.getName());

        updatePlayer1Stats();
        updatePlayer2Stats();

        abilitiesListP1.setText(character1.getAbilitiesDescription());
        abilitiesListP2.setText(character2.getAbilitiesDescription());

        abilitySelectorP1.removeAllItems();
        character1.getAbilities().forEach(ability ->
            abilitySelectorP1.addItem(ability.getName() + " (EP: " + ability.getCost() + ")")
        );

        abilitySelectorP2.removeAllItems();
        character2.getAbilities().forEach(ability ->
            abilitySelectorP2.addItem(ability.getName() + " (EP: " + ability.getCost() + ")")
        );
    }

    /**
     * Updates Player 1's stats dynamically.
     */
    public void updatePlayer1Stats() {
        lblPlayer1Stats.setText("HP: " + character1.getHp() + "/" + character1.getMaxHp()
                              + " | EP: " + character1.getEp() + "/" + character1.getMaxEp());
    }

    /**
     * Updates Player 2's stats dynamically.
     */
    public void updatePlayer2Stats() {
        lblPlayer2Stats.setText("HP: " + character2.getHp() + "/" + character2.getMaxHp()
                              + " | EP: " + character2.getEp() + "/" + character2.getMaxEp());
    }

    // --- GUI Helper Methods ---
    private JLabel createLabel(String text, int x, int y, int width, int height) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setBounds(x, y, width, height);
        return lbl;
    }

    private JTextArea createTextArea(int x, int y, int width, int height) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setOpaque(false);
        area.setForeground(Color.WHITE);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBounds(x, y, width, height);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        return area;
    }

    // --- Battle Log Updates ---
    public void appendToBattleLog(String text) {
        battleLogArea.append(text + "\n");
    }

    public void clearBattleLog() {
        battleLogArea.setText("");
    }

    // --- Ability Selectors ---
    public JComboBox<String> getAbilitySelectorP1() { return abilitySelectorP1; }
    public JComboBox<String> getAbilitySelectorP2() { return abilitySelectorP2; }

    // --- Action Listener Registration (wired via controller) ---
    public void addUseAbilityP1Listener(ActionListener listener) { btnUseAbilityP1.addActionListener(listener); }
    public void addUseAbilityP2Listener(ActionListener listener) { btnUseAbilityP2.addActionListener(listener); }
    public void addRematchListener(ActionListener listener) { btnRematch.addActionListener(listener); }
    public void addReturnListener(ActionListener listener) { btnReturn.addActionListener(listener); }
}