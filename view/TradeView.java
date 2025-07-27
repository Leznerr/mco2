package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image; // retained for background
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionListener;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JLabel;

// import controller._;

/**
 * The trading view for Fatal Fantasy: Tactics Game.
 */
public class TradeView extends JFrame {

    private model.core.Player merchant;
    private model.core.Player client;
    private model.core.Character merchantChar;
    private model.core.Character clientChar;
    private java.util.List<model.core.Character> merchantChars = new java.util.ArrayList<>();
    private java.util.List<model.core.Character> clientChars = new java.util.ArrayList<>();

    // Button labels
    public static final String TRADE = "Trade";
    public static final String RETURN = "Return";
    public static final String MERCHANT_SELECT = "MerchantSelect";
    public static final String CLIENT_SELECT = "ClientSelect";

    // UI components
    private JButton btnTrade, btnReturn;
    private final DefaultListModel<model.item.MagicItem> merchantListModel = new DefaultListModel<>();
    private final DefaultListModel<model.item.MagicItem> clientListModel = new DefaultListModel<>();
    private final JList<model.item.MagicItem> lstMerchantItems = new JList<>(merchantListModel);
    private final JList<model.item.MagicItem> lstClientItems = new JList<>(clientListModel);
    private final JComboBox<String> merchantDropdown = new JComboBox<>();
    private final JComboBox<String> clientDropdown = new JComboBox<>();
    private JTextArea tradeLogArea;
    
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

    /** Convenience constructor wiring the merchant and client players. */
    public TradeView(model.core.Player merchant, model.core.Player client) {
        this();
        this.merchant = merchant;
        this.client = client;
        System.out.println("TradingView created for merchant=" + merchant.getName() +
                ", client=" + client.getName());
        populateInitialData();
        updateTradeButtonState();
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

        centerPanel.add(Box.createVerticalStrut(60));
        OutlinedLabel tradeLogLabel = new OutlinedLabel("TRADE LOG");
        tradeLogLabel.setFont(new Font("Serif", Font.BOLD, 26));
        tradeLogLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(tradeLogLabel);
        centerPanel.add(Box.createVerticalStrut(20));

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
        btnTrade.setEnabled(false);
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

        panel.add(Box.createVerticalStrut(40));

        String logoPath = (ID == 1)
                ? "view/assets/PlayerTradingLogo.png"
                : "view/assets/TraderTradingLogo.png";
        ImageIcon logoIcon = new ImageIcon(logoPath);
        Image logoImg = logoIcon.getImage().getScaledInstance(300, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(logoLabel);

        panel.add(Box.createVerticalStrut(15));

        JComboBox<String> dropdown = (ID == 1) ? clientDropdown : merchantDropdown;
        panel.add(createDropdownPanel("Select Character:", dropdown));

        panel.add(Box.createVerticalStrut(15));

        // Item selection list
        JList<model.item.MagicItem> lst = (ID == 1) ? lstClientItems : lstMerchantItems;
        panel.add(createListPanel("Select item(s) to trade:", lst));

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
        list.setVisibleRowCount(6);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object value, int index, boolean s, boolean f) {
                super.getListCellRendererComponent(l, value, index, s, f);
                if (value instanceof model.item.MagicItem mi) {
                    setText((index + 1) + ". " + mi.getName());
                }
                return this;
            }
        });

        JScrollPane pane = new JScrollPane(list);
        pane.setOpaque(false);
        pane.getViewport().setOpaque(false);
        pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pane.setMaximumSize(new Dimension(250, 130));
        pane.setPreferredSize(new Dimension(250, 130));

        RoundedDisplayBox box = new RoundedDisplayBox();
        box.setLayout(new BorderLayout());
        box.add(pane, BorderLayout.CENTER);

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(box);

        return panel;
    }

    /**
     * Helper method to create dropdown panels with outlined labels
     *
     * @param labelText the text for the label
     * @param dropdown  the JComboBox to be added
     * @return a JPanel containing the label and dropdown
     */
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
        merchantDropdown.setActionCommand(MERCHANT_SELECT);
        clientDropdown.setActionCommand(CLIENT_SELECT);
        merchantDropdown.addActionListener(listener);
        clientDropdown.addActionListener(listener);

        lstMerchantItems.addListSelectionListener(e -> updateTradeButtonState());
        lstClientItems.addListSelectionListener(e -> updateTradeButtonState());
    }


    /**
     * Updates the selectable item list for the given side.
     */
    public void updateItemList(int ID, java.util.List<model.item.MagicItem> items) {
        DefaultListModel<model.item.MagicItem> model = (ID == 1) ? clientListModel : merchantListModel;
        model.clear();
        for (model.item.MagicItem m : items) {
            model.addElement(m);
        }
    }

    /** Updates the merchant side item list. */
    public void updateMerchantItems(java.util.List<model.item.MagicItem> items) {
        updateItemList(2, items);
    }

    /** Updates the client side item list. */
    public void updateClientItems(java.util.List<model.item.MagicItem> items) {
        updateItemList(1, items);
    }

    public void setMerchantCharacters(java.util.List<model.core.Character> chars) {
        merchantChars = new java.util.ArrayList<>(chars);
        merchantDropdown.removeAllItems();
        for (model.core.Character c : chars) {
            merchantDropdown.addItem(c.getName());
        }
        merchantDropdown.setSelectedIndex(chars.isEmpty() ? -1 : 0);
        merchantChar = getSelectedMerchantCharacter();
    }

    public void setClientCharacters(java.util.List<model.core.Character> chars) {
        clientChars = new java.util.ArrayList<>(chars);
        clientDropdown.removeAllItems();
        for (model.core.Character c : chars) {
            clientDropdown.addItem(c.getName());
        }
        clientDropdown.setSelectedIndex(chars.isEmpty() ? -1 : 0);
        clientChar = getSelectedClientCharacter();
    }


    /** Populates dropdowns and item lists with the initial characters. */
    private void populateInitialData() {
        if (client != null) {
            setClientCharacters(client.getCharacters());
        }
        if (merchant != null) {
            setMerchantCharacters(merchant.getCharacters());
        }
        refresh();
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

    /** Enables or disables the trade button based on current selections. */
    private void updateTradeButtonState() {
        boolean hasChars = getSelectedMerchantCharacter() != null && getSelectedClientCharacter() != null
                && getSelectedMerchantCharacter() != getSelectedClientCharacter();
        boolean hasItems = !lstMerchantItems.isSelectionEmpty() || !lstClientItems.isSelectionEmpty();
        btnTrade.setEnabled(hasChars && hasItems);
    }

    // ------------------------------------------------------------------
    // Public API for controller
    // ------------------------------------------------------------------

    /** Returns the currently selected merchant character. */
    public model.core.Character getSelectedMerchantCharacter() {
        int idx = merchantDropdown.getSelectedIndex();
        if (idx >= 0 && idx < merchantChars.size()) {
            merchantChar = merchantChars.get(idx);
        } else {
            merchantChar = null;
        }
        return merchantChar;
    }

    /** Returns the currently selected client character. */
    public model.core.Character getSelectedClientCharacter() {
        int idx = clientDropdown.getSelectedIndex();
        if (idx >= 0 && idx < clientChars.size()) {
            clientChar = clientChars.get(idx);
        } else {
            clientChar = null;
        }
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
        clientChar = getSelectedClientCharacter();
        merchantChar = getSelectedMerchantCharacter();
        if (clientChar != null) {
            updateClientItems(clientChar.getInventory().getAllItems());
        }
        if (merchantChar != null) {
            updateMerchantItems(merchantChar.getInventory().getAllItems());
        }
        resetFields();
        updateTradeButtonState();
    }

}
