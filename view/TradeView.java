package view;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;

import model.core.Character;
import model.item.MagicItem;

/**
 * Simplified trading window showing two characters and their inventories.
 * The left column represents the merchant while the right column represents
 * the client. Users may select one or more items from each side and trade
 * them using the Trade button. A log of completed trades is displayed in the
 * middle of the window.
 */
public class TradeView extends JFrame {

    public static final String TRADE = "Trade";
    public static final String RETURN = "Return";

    private final Character merchant;
    private final Character client;

    private final DefaultListModel<MagicItem> merchantModel = new DefaultListModel<>();
    private final DefaultListModel<MagicItem> clientModel = new DefaultListModel<>();

    private final JList<MagicItem> merchantItemList = new JList<>(merchantModel);
    private final JList<MagicItem> clientItemList = new JList<>(clientModel);

    private final JTextArea tradeLogArea = new JTextArea();
    private final JButton btnTrade = new RoundedButton(TRADE);
    private final JButton btnReturn = new RoundedButton(RETURN);

    /**
     * Creates the trading view for the given characters.
     */
    public TradeView(Character merchant, Character client) {
        super("Fatal Fantasy: Tactics | Trading");
        this.merchant = merchant;
        this.client = client;
        initUI();
        setSize(990, 529);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    private void initUI() {
        getContentPane().setLayout(new BorderLayout());
        add(createSidePanel(merchant, merchantItemList, "Merchant"), BorderLayout.WEST);
        add(createLogPanel(), BorderLayout.CENTER);
        add(createSidePanel(client, clientItemList, "Client"), BorderLayout.EAST);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottom.setOpaque(false);
        bottom.add(btnTrade);
        bottom.add(btnReturn);
        add(bottom, BorderLayout.SOUTH);

        refresh();
    }

    private JPanel createSidePanel(Character c, JList<MagicItem> list, String label) {
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(250, 350));

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(c.getName(), SwingConstants.CENTER);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel selectLabel = new JLabel("Select item to trade");
        selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(10));
        panel.add(name);
        panel.add(Box.createVerticalStrut(10));
        panel.add(selectLabel);
        panel.add(scroll);
        panel.add(Box.createVerticalGlue());
        panel.setPreferredSize(new Dimension(320, 400));
        return panel;
    }

    private JPanel createLogPanel() {
        tradeLogArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(tradeLogArea);
        scroll.setPreferredSize(new Dimension(350, 350));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scroll, BorderLayout.CENTER);
        panel.setOpaque(false);
        return panel;
    }

    /** Registers listeners for the Trade and Return buttons. */
    public void setActionListener(ActionListener l) {
        btnTrade.setActionCommand(TRADE);
        btnReturn.setActionCommand(RETURN);
        btnTrade.addActionListener(l);
        btnReturn.addActionListener(l);
    }

    /**
     * Reloads item lists from the characters and clears any selection.
     */
    public void refresh() {
        merchantModel.clear();
        for (MagicItem m : merchant.getInventory().getAllItems()) {
            merchantModel.addElement(m);
        }

        clientModel.clear();
        for (MagicItem m : client.getInventory().getAllItems()) {
            clientModel.addElement(m);
        }

        merchantItemList.clearSelection();
        clientItemList.clearSelection();
    }

    public List<MagicItem> getSelectedMerchantItems() {
        return merchantItemList.getSelectedValuesList();
    }

    public List<MagicItem> getSelectedClientItems() {
        return clientItemList.getSelectedValuesList();
    }

    public Character getMerchant() { return merchant; }
    public Character getClient() { return client; }

    public void appendTradeLog(String msg) {
        tradeLogArea.append(msg + System.lineSeparator());
    }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}

