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
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// import controller._;

/**
 * The trading view for Fatal Fantasy: Tactics Game.
 */
public class TradeView extends JFrame {

    private model.core.Player merchant;
    private model.core.Player client;
    private model.core.Character merchantChar;
    private model.core.Character clientChar;

    // Button labels
    public static final String TRADE = "Trade";
    public static final String RETURN = "Return";

    // UI components
    private JButton btnTrade, btnReturn;
    private final DefaultListModel<model.item.MagicItem> merchantListModel = new DefaultListModel<>();
    private final DefaultListModel<model.item.MagicItem> clientListModel = new DefaultListModel<>();
    private final JList<model.item.MagicItem> lstMerchantItems = new JList<>(merchantListModel);
    private final JList<model.item.MagicItem> lstClientItems = new JList<>(clientListModel);
    private JTextArea p0NameArea, t0NameArea;
    private JTextArea p0ItemsArea, t0ItemsArea, tradeLogArea;
    
    /**
     * Constructs the Trading UI of Fatal Fantasy: Tactics Game.
     */
    public TradeView() {
        super("Fatal Fantasy: Tactics | Trading");

        initUI();
        
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    TradeView.this,
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
        System.out.println("TradingView default constructor");
    }

    /** Convenience constructor wiring the merchant and client data. */
    public TradeView(model.core.Player merchant,
                     model.core.Character merchantChar,
                     model.core.Player client,
                     model.core.Character clientChar) {
        this();
        this.merchant = merchant;
        this.client = client;
        this.merchantChar = merchantChar;
        this.clientChar = clientChar;
        System.out.println("TradingView created for merchant="
                + (merchantChar != null ? merchantChar.getName() : "null") +
                ", client=" + (clientChar != null ? clientChar.getName() : "null"));
        populateInitialData();
        setVisible(true);
    }


    /**
     * Initializes the UI components and arranges them in the main layout.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private Image bg = new ImageIcon("view/assets/TradingBG.jpg").getImage();
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

        centerPanel.add(Box.createVerticalStrut(100));

        // Rounded display box for battle log
        RoundedDisplayBox tradeLogPanel = new RoundedDisplayBox();
        tradeLogPanel.setPreferredSize(new Dimension(400, 380));
        tradeLogPanel.setMaximumSize(new Dimension(400, 380));
        tradeLogPanel.setLayout(new BorderLayout());
        tradeLogPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Text area for battle log
        tradeLogArea = new JTextArea();
        tradeLogArea.setFont(new Font("Serif", Font.PLAIN, 18));
        tradeLogArea.setForeground(Color.WHITE);
        tradeLogArea.setOpaque(false);
        tradeLogArea.setEditable(false);
        tradeLogArea.setLineWrap(true);
        tradeLogArea.setWrapStyleWord(true);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(tradeLogArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tradeLogPanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tradeLogPanel);
        centerPanel.add(Box.createVerticalStrut(30));

        // Bottom Panel (buttons)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 50));
        buttonPanel.setOpaque(false);

        btnTrade = new RoundedButton(TRADE);
        btnReturn = new RoundedButton(RETURN);

        buttonPanel.add(btnTrade);
        buttonPanel.add(btnReturn);

        // Left & Right Panels
        setupPanel(leftPanel, 1);
        setupPanel(rightPanel, 2);

        backgroundPanel.add(leftPanel, BorderLayout.WEST);
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        backgroundPanel.add(rightPanel, BorderLayout.EAST);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);


        setContentPane(backgroundPanel);
    }


    /**
     * Helper method to set up the player & trader panel with its components.
     * 
     * @param panel the panel to set up
     * @param ID the ID of the player or trader
     */
    private void setupPanel(JPanel panel, int ID) {
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Dimension fixedPanelSize = new Dimension(320, 700);
        panel.setPreferredSize(fixedPanelSize);
        panel.setMinimumSize(fixedPanelSize);
        panel.setMaximumSize(fixedPanelSize);

        panel.add(Box.createVerticalStrut(60));

        String headlineImagePath;

        // Logo
        if (ID == 1) {
            headlineImagePath = String.format("view/assets/PlayerTradingLogo.png");
        } else {
            headlineImagePath = String.format("view/assets/TraderTradingLogo.png");

        }
        ImageIcon logoIcon = new ImageIcon(headlineImagePath);
        Image logoImg = logoIcon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(logoLabel);

        panel.add(Box.createVerticalStrut(10));

        // Name Area
        RoundedDisplayBox namePanel = new RoundedDisplayBox();
        namePanel.setPreferredSize(new Dimension(280, 40));
        namePanel.setMaximumSize(new Dimension(280, 40));
        namePanel.setMinimumSize(new Dimension(280, 40));
        namePanel.setLayout(new BorderLayout());
        namePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea nameArea;
        if (ID == 1) {
            if (p0NameArea == null) p0NameArea = new JTextArea();
            nameArea = p0NameArea;
        } else {
            if (t0NameArea == null) t0NameArea = new JTextArea();
            nameArea = t0NameArea;
        }
        nameArea.setFont(new Font("Serif", Font.PLAIN, 18));
        nameArea.setForeground(Color.WHITE);
        nameArea.setOpaque(false);
        nameArea.setEditable(false);
        nameArea.setLineWrap(true);
        nameArea.setWrapStyleWord(true);
        namePanel.add(nameArea, BorderLayout.CENTER);

        panel.add(Box.createVerticalStrut(10));
        panel.add(namePanel);

        // Abilities/Items Area
        RoundedDisplayBox itemsPanel = new RoundedDisplayBox();
        itemsPanel.setPreferredSize(new Dimension(280, 200));
        itemsPanel.setMaximumSize(new Dimension(280, 200));
        itemsPanel.setMinimumSize(new Dimension(280, 200));
        itemsPanel.setLayout(new BorderLayout());
        itemsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea itemsArea;
        if (ID == 1) {
            if (p0ItemsArea == null) {
                p0ItemsArea = new JTextArea();
            }
            itemsArea = p0ItemsArea;
        } else {
            if (t0ItemsArea == null) {
                t0ItemsArea = new JTextArea();
            }
            itemsArea = t0ItemsArea;
        }
        itemsArea.setFont(new Font("Serif", Font.PLAIN, 18));
        itemsArea.setForeground(Color.WHITE);
        itemsArea.setOpaque(false);
        itemsArea.setEditable(false);
        itemsArea.setLineWrap(true);
        itemsArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(itemsArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        itemsPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(Box.createVerticalStrut(20));
        panel.add(itemsPanel);

        // Item selection list
        panel.add(Box.createVerticalStrut(30));
        JList<model.item.MagicItem> lst;
        if (ID == 1) {
            lst = lstMerchantItems;
        } else {
            lst = lstClientItems;
        }
        panel.add(createListPanel("Select magic item(s) to trade:", lst));

        panel.add(Box.createVerticalGlue());
    }


    /**
     * Helper method to create item selection panels with outlined labels
     *
     * @param labelText the text for the label
     * @param list      the JList component to embed
     * @return a JPanel containing the label and list
     */
    private JPanel createListPanel(String labelText, JList<model.item.MagicItem> list) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        OutlinedLabel label = new OutlinedLabel(labelText);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Serif", Font.BOLD, 17));

        list.setFont(new Font("Serif", Font.BOLD, 18));
        list.setVisibleRowCount(5);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object value, int index, boolean s, boolean f) {
                super.getListCellRendererComponent(l, value, index, s, f);
                if (value instanceof model.item.MagicItem mi) {
                    setText(mi.getName());
                }
                return this;
            }
        });

        JScrollPane pane = new JScrollPane(list);
        pane.setMaximumSize(new Dimension(250, 100));
        pane.setPreferredSize(new Dimension(250, 100));

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(pane);

        return panel;
    }


    /**
     * Sets the action listener for the button click events.
     * 
     * @param listener The listener
     */
    public void setActionListener(ActionListener listener) {
        // Bottom Panel Buttons
        btnTrade.addActionListener(listener);
        btnReturn.addActionListener(listener);
        btnTrade.setActionCommand(TRADE);
        btnReturn.setActionCommand(RETURN);
    }


    /**
     * Updates the selectable item list for the given side.
     */
    public void updateItemList(int ID, java.util.List<model.item.MagicItem> items) {
        DefaultListModel<model.item.MagicItem> model = (ID == 1) ? merchantListModel : clientListModel;
        model.clear();
        for (model.item.MagicItem m : items) {
            model.addElement(m);
        }
    }


    /**
     * Sets the player name and trader name
     * 
     * @param ID the ID of the player or trader
     * @param text the text to set
     */
    public void setPlayerTraderName(int ID, String text) {
        switch (ID) {
            case 1 -> p0NameArea.setText(text);
            case 2 -> t0NameArea.setText(text);
        }
    }


    /**
     * Sets the items for the player and trader
     * 
     * @param ID the ID of the player or trader
     * @param items the items text
     */
    public void setPlayerTraderItems(int ID, String items) {
        switch (ID) {
            case 1 -> p0ItemsArea.setText(items);
            case 2 -> t0ItemsArea.setText(items);
        }
    }

    /** Populates UI fields with the merchant and client data. */
    private void populateInitialData() {
        if (merchantChar != null) {
            setPlayerTraderName(1, merchantChar.getName());
            setPlayerTraderItems(1, buildItemsList(merchantChar));
            updateItemList(1, merchantChar.getInventory().getAllItems());
        }
        if (clientChar != null) {
            setPlayerTraderName(2, clientChar.getName());
            setPlayerTraderItems(2, buildItemsList(clientChar));
            updateItemList(2, clientChar.getInventory().getAllItems());
        }
    }

    private String buildItemsList(model.core.Character c) {
        return c.getInventory().getAllItems().stream()
                .map(model.item.MagicItem::getName)
                .collect(java.util.stream.Collectors.joining(", "));
    }


    /**
     * Appends text to the trade log
     * 
     * @param text the text to append
     */
    public void appendTradeLog(String text) {
        tradeLogArea.append(text + "\n");
    }
    

    /** Resets item selections. */
    public void resetFields() {
        lstMerchantItems.clearSelection();
        lstClientItems.clearSelection();
    }

    // ------------------------------------------------------------------
    // Public API for controller
    // ------------------------------------------------------------------

    /** Returns the currently active merchant character. */
    public model.core.Character getMerchant() {
        return merchantChar;
    }

    /** Returns the currently active client character. */
    public model.core.Character getClient() {
        return clientChar;
    }

    /** All selected items from the merchant list. */
    public java.util.List<model.item.MagicItem> getSelectedMerchantItems() {
        return lstMerchantItems.getSelectedValuesList();
    }

    /** All selected items from the client list. */
    public java.util.List<model.item.MagicItem> getSelectedClientItems() {
        return lstClientItems.getSelectedValuesList();
    }

    /** Display an error dialog. */
    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Display an info dialog. */
    public void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Refreshes inventory displays and item lists. */
    public void refresh() {
        if (merchantChar != null) {
            setPlayerTraderItems(1, buildItemsList(merchantChar));
            updateItemList(1, merchantChar.getInventory().getAllItems());
        }
        if (clientChar != null) {
            setPlayerTraderItems(2, buildItemsList(clientChar));
            updateItemList(2, clientChar.getInventory().getAllItems());
        }
        resetFields();
    }

}
