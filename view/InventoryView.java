package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

// import controller._;

/**
 * The inventory view for Fatal Fantasy: Tactics Game.
 */
public class InventoryView extends JFrame{
    private int playerID;

    // Button labels
    public static final String EQUIP = "Equip";
    public static final String UNEQUIP = "Unequip";
    public static final String VIEW_ITEM = "View Magic Item";
    public static final String BACK = "Back to Inventory";
    public static final String RETURN = "Return";

    // UI components
    private JButton btnEquip;
    private JButton btnUnequip;

    private JButton btnReturn;
    private JButton btnViewItem;
    private JButton btnBack;

    private JLabel lblName;
    private JLabel lblType;
    private JLabel lblStatus;
    private JLabel lblRarity;
    private JLabel lblEffect;
    private JTextArea txtDescription;

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private final DefaultListModel<model.item.MagicItem> listModel = new DefaultListModel<>();
    private JList<model.item.MagicItem> itemList;


    /**
     * Constructs the Inventory UI of Fatal Fantasy: Tactics Game.
     */
    public InventoryView(int playerID) {
        super("Fatal Fantasy: Tactics | Player " + playerID + " Inventory");

        this.playerID = playerID;

        initUI();
        
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    InventoryView.this,
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
     * Initializes the UI components and arranges them in the main layout.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private Image bg = new ImageIcon("view/assets/InventoryBG.jpg").getImage();

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

        // Center panel for headline + display box
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalStrut(30)); 

        String headlineImagePath = String.format("view/assets/Player%dInventoryLogo.png", playerID);
        ImageIcon logoIcon = new ImageIcon(headlineImagePath);     
        Image logoImg = logoIcon.getImage().getScaledInstance(380, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);

        centerPanel.add(Box.createVerticalStrut(10));

        // Rounded display box for list/details
        RoundedDisplayBox detailsPanel = new RoundedDisplayBox();
        detailsPanel.setPreferredSize(new Dimension(400, 500));
        detailsPanel.setMaximumSize(new Dimension(400, 500));
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        itemList = new JList<>(listModel);
        itemList.setOpaque(false);
        itemList.setFont(new Font("Serif", Font.BOLD, 18));
        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object val, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(l, val, idx, sel, foc);
                setOpaque(false);
                if (val instanceof model.item.MagicItem mi) {
                    setText((idx + 1) + ". " + mi.getName());
                    setToolTipText(mi.getDescription());
                    setForeground(Color.WHITE);
                }
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(itemList);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel tipLabel = new JLabel("Single-use items may only be used in battle.");
        tipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setOpaque(false);
        listPanel.add(scrollPane, BorderLayout.CENTER);
        listPanel.add(tipLabel, BorderLayout.SOUTH);

        // Detail panel setup
        JPanel detailPanel = new JPanel();
        detailPanel.setOpaque(false);
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));

        lblName = new JLabel();
        lblName.setFont(new Font("Serif", Font.BOLD, 22));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setForeground(Color.WHITE);
        lblType = new JLabel();
        lblType.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblType.setForeground(Color.WHITE);
        lblStatus = new JLabel();
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStatus.setForeground(Color.WHITE);
        lblRarity = new JLabel();
        lblRarity.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblRarity.setForeground(Color.WHITE);
        lblEffect = new JLabel();
        lblEffect.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblEffect.setForeground(Color.WHITE);

        txtDescription = new JTextArea();
        txtDescription.setFont(new Font("Serif", Font.PLAIN, 18));
        txtDescription.setForeground(Color.WHITE);
        txtDescription.setOpaque(false);
        txtDescription.setEditable(false);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setMinimumSize(new Dimension(350, 40));
        txtDescription.setPreferredSize(new Dimension(350, 60));

        JScrollPane descPane = new JScrollPane(txtDescription);
        descPane.setOpaque(false);
        descPane.getViewport().setOpaque(false);
        descPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        descPane.setMinimumSize(new Dimension(350, 40));
        descPane.setPreferredSize(new Dimension(350, 60));

        detailPanel.add(Box.createVerticalStrut(10));
        detailPanel.add(lblName);
        detailPanel.add(Box.createVerticalStrut(5));
        detailPanel.add(lblType);
        detailPanel.add(lblStatus);
        detailPanel.add(lblRarity);
        detailPanel.add(lblEffect);
        detailPanel.add(Box.createVerticalStrut(10));
        detailPanel.add(descPane);
        detailPanel.add(Box.createVerticalGlue());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.add(listPanel, "LIST");
        cardPanel.add(detailPanel, "DETAIL");
        cardLayout.show(cardPanel, "LIST");

        detailsPanel.add(cardPanel, BorderLayout.CENTER);

        centerPanel.add(detailsPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        btnViewItem = new RoundedButton(VIEW_ITEM);
        btnBack = new RoundedButton(BACK);
        btnBack.setVisible(false);
        centerPanel.add(btnViewItem);
        centerPanel.add(btnBack);
        centerPanel.add(Box.createVerticalGlue());

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 50));
        buttonPanel.setOpaque(false);

        btnEquip = new RoundedButton(EQUIP);
        btnUnequip = new RoundedButton(UNEQUIP);

        btnReturn = new RoundedButton(RETURN);

        buttonPanel.add(btnEquip);
        buttonPanel.add(btnUnequip);
        buttonPanel.add(btnReturn);

        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(backgroundPanel);
    }


    /**
     * Sets the action listener for the button click events.
     * 
     * @param listener The listener
     */
    public void setActionListener(ActionListener listener) {
        btnEquip.setActionCommand(EQUIP);
        btnUnequip.setActionCommand(UNEQUIP);
        btnReturn.setActionCommand(RETURN);
        btnViewItem.setActionCommand(VIEW_ITEM);
        btnBack.setActionCommand(BACK);

        btnEquip.addActionListener(listener);
        btnUnequip.addActionListener(listener);
        btnReturn.addActionListener(listener);
        btnViewItem.addActionListener(listener);
        btnBack.addActionListener(listener);
    }

    /** Updates the inventory list and highlights the equipped item. */
    public void updateInventory(java.util.List<model.item.MagicItem> items,
                                model.item.MagicItem equipped) {
        showInventoryList();
        listModel.clear();
        for (model.item.MagicItem m : items) listModel.addElement(m);
        itemList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object val, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(l, val, idx, sel, foc);
                setOpaque(false);
                if (val instanceof model.item.MagicItem mi) {
                    String name = mi.getName();
                    if (mi.equals(equipped)) name += " (Equipped)";
                    setText((idx + 1) + ". " + name);
                    setToolTipText(mi.getDescription());
                    setForeground(Color.WHITE);
                }
                return this;
            }
        });
    }

    /** Returns the currently selected item or {@code null}. */
    public model.item.MagicItem getSelectedItem() {
        return itemList.getSelectedValue();
    }

    /** Shows the detail panel for the provided item. */
    public void displayItemDetails(model.item.MagicItem item, boolean isEquipped) {
        if (item == null) return;
        lblName.setText(item.getName());
        lblType.setText("Type: " + item.getItemType());
        lblStatus.setText("Status: " + (isEquipped ? "Equipped" : "Not Equipped"));
        lblRarity.setText("Rarity: " + item.getRarity());
        if (item instanceof model.item.SingleUseItem su) {
            lblEffect.setText("Effect: " + describeEffect(su));
        } else {
            lblEffect.setText("");
        }
        txtDescription.setText(item.getDescription());
        txtDescription.setForeground(Color.WHITE);
        cardLayout.show(cardPanel, "DETAIL");
        btnViewItem.setVisible(false);
        btnBack.setVisible(true);
    }

    /** Returns to the inventory list view. */
    public void showInventoryList() {
        cardLayout.show(cardPanel, "LIST");
        btnViewItem.setVisible(true);
        btnBack.setVisible(false);
    }

    private String describeEffect(model.item.SingleUseItem item) {
        return switch (item.getEffectType()) {
            case HEAL_HP -> "Heals " + item.getEffectValue() + " HP";
            case RESTORE_EP -> "Restores " + item.getEffectValue() + " EP";
            case REVIVE -> "Revives with " + item.getEffectValue() + "% HP";
            case GRANT_IMMUNITY -> "Grants immunity for " + item.getEffectValue() + " turn(s)";
        };
    }

    public void showInfoMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}