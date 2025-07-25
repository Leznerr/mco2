package view;

import model.core.Character;
import model.item.MagicItem;
import model.item.SingleUseItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Graphical battle arena for two characters.
 * <p>
 * Displays player panels with ability buttons, item usage, defend/recharge
 * actions, a combat log, and a battle outcome box. The view exposes methods
 * for controllers to attach listeners and to update HP/EP/status in real time.
 * </p>
 */
public class BattleView extends JPanel {

    private final Character character1;
    private final Character character2;

    private final PlayerPanel panelP1;
    private final PlayerPanel panelP2;

    private final JTextArea combatLogArea = new JTextArea();
    private final JTextArea outcomeArea   = new JTextArea();

    private final JButton btnRematch = new JButton("Rematch");
    private final JButton btnReturn  = new JButton("Return to Main Menu");

    /**
     * Constructs the battle view for the given characters.
     */
    public BattleView(Character c1, Character c2) {
        this.character1 = c1;
        this.character2 = c2;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        panelP1 = new PlayerPanel(c1);
        panelP2 = new PlayerPanel(c2);

        initLayout();
        updatePlayerPanels();
    }

    private void initLayout() {
        JPanel background = new JPanel() {
            private final Image bg = new ImageIcon("view/assets/CharSelectAndBattleBG.jpg").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        background.setLayout(new BorderLayout());

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);

        combatLogArea.setEditable(false);
        combatLogArea.setOpaque(false);
        combatLogArea.setForeground(Color.WHITE);
        combatLogArea.setLineWrap(true);
        combatLogArea.setWrapStyleWord(true);

        JScrollPane logScroll = new JScrollPane(combatLogArea);
        logScroll.setOpaque(false);
        logScroll.getViewport().setOpaque(false);
        center.add(logScroll, BorderLayout.CENTER);

        outcomeArea.setEditable(false);
        outcomeArea.setOpaque(false);
        outcomeArea.setForeground(Color.YELLOW);
        outcomeArea.setVisible(false);
        center.add(outcomeArea, BorderLayout.EAST);

        background.add(panelP1, BorderLayout.WEST);
        background.add(panelP2, BorderLayout.EAST);
        background.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(btnRematch);
        bottom.add(btnReturn);
        background.add(bottom, BorderLayout.SOUTH);

        add(background, BorderLayout.CENTER);
    }

    /** Updates labels for both player panels. */
    public void updatePlayerPanels() {
        panelP1.updateStats();
        panelP2.updateStats();
        panelP1.updateItemDisplay();
        panelP2.updateItemDisplay();
    }

    /* ------------------------------------------------------------------ */
    /* Public view operations                                             */
    /* ------------------------------------------------------------------ */

    public void displayBattleStart(Character c1, Character c2) {
        combatLogArea.setText("");
        appendToCombatLog("-- Battle Start --");
        appendToCombatLog(c1.getName() + " vs " + c2.getName());
        updatePlayerPanels();
    }

    public void displayTurnResults(CombatLog log) {
        combatLogArea.setText(String.join("\n", log.getLogEntries()));
        updatePlayerPanels();
    }

    public void displayBattleEnd(Character winner) {
        appendToCombatLog(winner.getName() + " wins the battle!");
        outcomeArea.setText("Winner: " + winner.getName());
        outcomeArea.setVisible(true);
        lockPlayerControls();
    }

    public void appendToCombatLog(String text) {
        combatLogArea.append(text + "\n");
    }

    /* ------------------------------------------------------------------ */
    /* Listener registration helpers                                      */
    /* ------------------------------------------------------------------ */

    public void addAbilityListenerP1(int index, ActionListener l) {
        panelP1.getAbilityButton(index).addActionListener(l);
    }

    public void addAbilityListenerP2(int index, ActionListener l) {
        panelP2.getAbilityButton(index).addActionListener(l);
    }

    public void addDefendListenerP1(ActionListener l) { panelP1.defendButton.addActionListener(l); }
    public void addDefendListenerP2(ActionListener l) { panelP2.defendButton.addActionListener(l); }
    public void addRechargeListenerP1(ActionListener l) { panelP1.rechargeButton.addActionListener(l); }
    public void addRechargeListenerP2(ActionListener l) { panelP2.rechargeButton.addActionListener(l); }
    public void addUseItemListenerP1(ActionListener l) { panelP1.useItemButton.addActionListener(l); }
    public void addUseItemListenerP2(ActionListener l) { panelP2.useItemButton.addActionListener(l); }
    public void addRematchListener(ActionListener l)   { btnRematch.addActionListener(l); }
    public void addReturnListener(ActionListener l)    { btnReturn.addActionListener(l); }

    /** Enables or disables player two's controls (for AI battles). */
    public void setPlayer2ControlsEnabled(boolean enabled) {
        panelP2.setControlsEnabled(enabled);
    }

    /** Disables all action buttons for both players. */
    public void lockPlayerControls() {
        panelP1.setControlsEnabled(false);
        panelP2.setControlsEnabled(false);
    }

    /* ------------------------------------------------------------------ */
    /* Inner PlayerPanel class                                            */
    /* ------------------------------------------------------------------ */

    private static class PlayerPanel extends JPanel {
        private final Character character;
        private final JLabel nameLabel = new JLabel();
        private final JLabel classLabel = new JLabel();
        private final JLabel hpLabel = new JLabel();
        private final JLabel epLabel = new JLabel();
        private final JLabel itemLabel = new JLabel();
        private final List<JButton> abilityButtons = new ArrayList<>(3);
        private final JButton defendButton = new JButton("Defend");
        private final JButton rechargeButton = new JButton("Recharge");
        private final JButton useItemButton = new JButton("Use Item");

        PlayerPanel(Character c) {
            this.character = c;
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            nameLabel.setForeground(Color.WHITE);
            classLabel.setForeground(Color.WHITE);
            hpLabel.setForeground(Color.WHITE);
            epLabel.setForeground(Color.WHITE);
            itemLabel.setForeground(Color.WHITE);

            add(nameLabel);
            add(classLabel);
            add(hpLabel);
            add(epLabel);
            add(itemLabel);

            for (int i = 0; i < 3 && i < c.getAbilities().size(); i++) {
                JButton b = new JButton(c.getAbilities().get(i).getName());
                abilityButtons.add(b);
                add(b);
            }
            add(defendButton);
            add(rechargeButton);
            add(useItemButton);
        }

        JButton getAbilityButton(int idx) { return abilityButtons.get(idx); }

        void setControlsEnabled(boolean enabled) {
            for (JButton b : abilityButtons) b.setEnabled(enabled);
            defendButton.setEnabled(enabled);
            rechargeButton.setEnabled(enabled);
            useItemButton.setEnabled(enabled);
        }

        void updateStats() {
            nameLabel.setText(character.getName());
            classLabel.setText(character.getClassType() + " " + character.getRaceType());
            hpLabel.setText("HP: " + character.getCurrentHp() + "/" + character.getMaxHp());
            epLabel.setText("EP: " + character.getCurrentEp() + "/" + character.getMaxEp());
        }

        void updateItemDisplay() {
            MagicItem item = character.getEquippedItem();
            if (item == null) {
                itemLabel.setText("Item: None");
                useItemButton.setEnabled(false);
            } else {
                itemLabel.setText("Item: " + item.getName());
                useItemButton.setEnabled(item instanceof SingleUseItem);
            }
        }
    }
}
