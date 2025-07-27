package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

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
    public static final String CANCEL = "CANCEL";

    private final Player player1;
    private final Player player2;

    private final JComboBox<Character> charBox1;
    private final JComboBox<Character> charBox2;

    private final DefaultListModel<MagicItem> model1;
    private final DefaultListModel<MagicItem> model2;
    private final JList<MagicItem> list1;
    private final JList<MagicItem> list2;

    private final JButton btnTrade;
    private final JButton btnCancel;

    public TradeView(Player p1, Player p2) {
        super("Trade Items");
        this.player1 = p1;
        this.player2 = p2;

        charBox1 = new JComboBox<>(p1.getCharacters().toArray(new Character[0]));
        charBox2 = new JComboBox<>(p2.getCharacters().toArray(new Character[0]));

        model1 = new DefaultListModel<>();
        model2 = new DefaultListModel<>();
        list1 = new JList<>(model1);
        list2 = new JList<>(model2);
        list1.setVisibleRowCount(8);
        list2.setVisibleRowCount(8);
        list1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list2.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        btnTrade = new RoundedButton("Trade");
        btnCancel = new RoundedButton("Cancel");

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

        JPanel left = new JPanel(new BorderLayout());
        left.add(createDropdownPanel("", charBox1), BorderLayout.NORTH);
        left.add(new JScrollPane(list1), BorderLayout.CENTER);
        lists.add(left);

        JPanel right = new JPanel(new BorderLayout());
        right.add(createDropdownPanel("", charBox2), BorderLayout.NORTH);
        right.add(new JScrollPane(list2), BorderLayout.CENTER);
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
        model1.clear();
        Character c1 = getSelectedChar1();
        if (c1 != null) {
            for (MagicItem m : c1.getInventory().getAllItems()) {
                model1.addElement(m);
            }
        }

        model2.clear();
        Character c2 = getSelectedChar2();
        if (c2 != null) {
            for (MagicItem m : c2.getInventory().getAllItems()) {
                model2.addElement(m);
            }
        }
    }

    public Character getSelectedChar1() {
        return (Character) charBox1.getSelectedItem();
    }

    public Character getSelectedChar2() {
        return (Character) charBox2.getSelectedItem();
    }

    public List<MagicItem> getSelectedItems1() {
        return list1.getSelectedValuesList();
    }

    public List<MagicItem> getSelectedItems2() {
        return list2.getSelectedValuesList();
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
}

