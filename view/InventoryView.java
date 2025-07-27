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
    public static final String RETURN = "Return";

    // UI components
    private JButton btnEquip;
    private JButton btnUnequip;
    private JButton btnReturn;
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

        // Rounded display box for character list
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
                if (val instanceof model.item.MagicItem mi) {
                    setText((idx + 1) + ". " + mi.getName());
                }
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(itemList);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        detailsPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel tipLabel = new JLabel("Single-use items may only be used in battle.");
        tipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(tipLabel, BorderLayout.SOUTH);

        centerPanel.add(detailsPanel);
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

        btnEquip.addActionListener(listener);
        btnUnequip.addActionListener(listener);
        btnReturn.addActionListener(listener);
    }

    /** Updates the inventory list and highlights the equipped item. */
    public void updateInventory(java.util.List<model.item.MagicItem> items,
                                model.item.MagicItem equipped) {
        listModel.clear();
        for (model.item.MagicItem m : items) listModel.addElement(m);
        itemList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object val, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(l, val, idx, sel, foc);
                if (val instanceof model.item.MagicItem mi) {
                    String name = mi.getName();
                    if (mi.equals(equipped)) name += " (Equipped)";
                    setText((idx + 1) + ". " + name);
                }
                return this;
            }
        });
    }

    /** Returns the currently selected item or {@code null}. */
    public model.item.MagicItem getSelectedItem() {
        return itemList.getSelectedValue();
    }

    public void showInfoMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}