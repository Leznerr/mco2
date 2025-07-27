package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import javax.swing.ListSelectionModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.core.Character;
import model.core.Player;
import model.item.MagicItem;

/**
 * Simple GUI for trading magic items between two players.
 * <p>
 * Purely responsible for presenting item lists and collecting
 * user selections. All validation and persistence are handled
 * in the controller layer.
 */
public class TradeView extends JFrame {

    public static final String TRADE = "TRADE";
    /** Action command for cancelling/returning from the view. */
    public static final String CANCEL = "Cancel";

    private final Player player1;
    private final Player player2;

    private final JComboBox<Character> charBox1;
    private final JComboBox<Character> charBox2;

    private final javax.swing.JList<MagicItem> itemList1;
    private final javax.swing.JList<MagicItem> itemList2;

    private final JButton btnTrade;
    private final JButton btnCancel;

    public TradeView(Player p1, Player p2) {
        super("Trade Items");
        this.player1 = p1;
        this.player2 = p2;

        charBox1 = new JComboBox<>(p1.getCharacters().toArray(new Character[0]));
        charBox2 = new JComboBox<>(p2.getCharacters().toArray(new Character[0]));

        itemList1 = new javax.swing.JList<>();
        itemList2 = new javax.swing.JList<>();

        itemList1.setCellRenderer(new ItemRenderer());
        itemList2.setCellRenderer(new ItemRenderer());
        itemList1.setVisibleRowCount(8);
        itemList2.setVisibleRowCount(8);
        itemList1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        itemList2.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        btnTrade = new RoundedButton("Trade");
        btnCancel = new RoundedButton("Return");

        initUI();
        configureWindow();
        bindComboListeners();
        refreshLists();
    }

    private void configureWindow() {
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout());

        JPanel lists = new JPanel(new GridLayout(1, 2, 10, 10));
        lists.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(createDropdownPanel("Character", charBox1));
        left.add(Box.createVerticalStrut(10));
        left.add(createListPanel("Items", itemList1));
        lists.add(left);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(createDropdownPanel("Character", charBox2));
        right.add(Box.createVerticalStrut(10));
        right.add(createListPanel("Items", itemList2));
        lists.add(right);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttons.add(btnTrade);
        buttons.add(btnCancel);

        main.add(lists, BorderLayout.CENTER);
        main.add(buttons, BorderLayout.SOUTH);

        setContentPane(main);
    }

    private void bindComboListeners() {
        charBox1.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                refreshLists();
            }
        });
        charBox2.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                refreshLists();
            }
        });
    }

    public void refreshLists() {
        Character c1 = getSelectedChar1();
        Character c2 = getSelectedChar2();

        javax.swing.DefaultListModel<MagicItem> m1 = new javax.swing.DefaultListModel<>();
        if (c1 != null) {
            for (MagicItem m : c1.getInventory().getAllItems()) {
                m1.addElement(m);
            }
        }
        itemList1.setModel(m1);

        javax.swing.DefaultListModel<MagicItem> m2 = new javax.swing.DefaultListModel<>();
        if (c2 != null) {
            for (MagicItem m : c2.getInventory().getAllItems()) {
                m2.addElement(m);
            }
        }
        itemList2.setModel(m2);
    }

    public Character getSelectedChar1() {
        return (Character) charBox1.getSelectedItem();
    }

    public Character getSelectedChar2() {
        return (Character) charBox2.getSelectedItem();
    }

    public java.util.List<MagicItem> getSelectedItems1() {
        return itemList1.getSelectedValuesList();
    }

    public java.util.List<MagicItem> getSelectedItems2() {
        return itemList2.getSelectedValuesList();
    }

    public void setActionListener(ActionListener l) {
        btnTrade.setActionCommand(TRADE);
        btnCancel.setActionCommand(CANCEL);
        btnTrade.addActionListener(l);
        btnCancel.addActionListener(l);
    }

    public void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JPanel createDropdownPanel(String labelText, JComboBox<?> dropdown) {
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
        Dimension ddSize = new Dimension(350, 40);
        dropdown.setPreferredSize(ddSize);
        dropdown.setMaximumSize(ddSize);
        dropdown.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        panel.add(label);
        panel.add(dropdown);
        return panel;
    }

    private JPanel createListPanel(String labelText, javax.swing.JList<?> list) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        OutlinedLabel label = new OutlinedLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(list);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setPreferredSize(new Dimension(350, 150));

        panel.add(label);
        panel.add(scroll);
        return panel;
    }

    /** Simple list cell renderer showing item details. */
    private static class ItemRenderer extends javax.swing.DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(javax.swing.JList<?> list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof MagicItem m) {
                String text = m.getName() + " [" + m.getItemType() + "]";
                if (m instanceof model.item.SingleUseItem su) {
                    text += " - " + su.getEffectType() + " " + su.getEffectValue();
                }
                setText(text);
            }
            return this;
        }
    }
}

