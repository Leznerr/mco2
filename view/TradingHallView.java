package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// import controller._;

/**
 * The trading hall view for Fatal Fantasy: Tactics Game.
 */
public class TradingHallView extends JFrame {
    // Button labels
    public static final String START_TRADING = "Start Trading";
    public static final String RETURN_TO_MENU = "Return to Menu";

    // UI components
    private JComboBox<String> dropdown1 = new JComboBox<>();
    private JComboBox<String> dropdown2 = new JComboBox<>();
    private JButton btnStartTrading;
    private JButton btnReturnToMenu;
    
    
    /**
     * Constructs the TradingHallView UI of Fatal Fantasy: Tactics Game.
     */
    public TradingHallView() {
        super("Fatal Fantasy: Tactics | Trading Hall");

        initUI();
        
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    TradingHallView.this,
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
                int y = (panelHeight - height) / 2;
                g.drawImage(bg, x, y, width, height, this);
            }
        };

        backgroundPanel.setLayout(new BorderLayout());

        // Center panel for logo and text fields
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        centerPanel.add(Box.createVerticalStrut(100));
        ImageIcon logoIcon = new ImageIcon("view/assets/TradingHallLogo.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(500, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        
        centerPanel.add(Box.createVerticalStrut(40));

        // Dropdown Panels with Outlined Labels
        centerPanel.add(createDropdownPanel("Select Merchant:      ", dropdown1));
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(createDropdownPanel("Select Client:     ", dropdown2));

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 50));
        buttonPanel.setOpaque(false);

        btnStartTrading = new RoundedButton(START_TRADING);
        btnStartTrading.setEnabled(false);
        btnReturnToMenu = new RoundedButton(RETURN_TO_MENU);

        buttonPanel.add(btnStartTrading);
        buttonPanel.add(btnReturnToMenu);

        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(backgroundPanel);
    }


    /**
     * Helper method to create dropdown panels with outlined labels
     * 
     * @param labelText the text for the label
     * @param dropdown the JComboBox to be added
     * @return a JPanel containing the label and dropdown
     */
    private JPanel createDropdownPanel(String labelText, JComboBox<String> dropdown) {
        JPanel dropdownPanel = new JPanel();
        dropdownPanel.setOpaque(false);
        dropdownPanel.setLayout(new BoxLayout(dropdownPanel, BoxLayout.X_AXIS));
        dropdownPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        OutlinedLabel label = new OutlinedLabel(labelText);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        // Force all labels to same preferred width
        int fixedLabelWidth = 140; // adjust as needed
        Dimension labelSize = new Dimension(fixedLabelWidth, label.getPreferredSize().height);
        label.setPreferredSize(labelSize);
        label.setMinimumSize(labelSize);
        label.setMaximumSize(labelSize);

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
        btnStartTrading.addActionListener(listener);
        btnReturnToMenu.addActionListener(listener);
        dropdown1.addActionListener(listener);
        dropdown2.addActionListener(listener);
    }


    /**
     * Sets the available players as merchant in the dropdown.
     * 
     * @param options The array of player merchant options
     */
    public void setMerchantOptions(String[] options) {
        dropdown1.removeAllItems();
        for (String opt : options) dropdown1.addItem(opt);
    }


    /**
     * Sets the available players as client in the dropdown.
     * 
     * @param options The array of player client options
     */
    public void setClientOptions(String[] options) {
        dropdown2.removeAllItems();
        for (String opt : options) dropdown2.addItem(opt);
    }


    /**
     * Resets all dropdown selections.
     */
    public void resetDropdowns() {
        dropdown1.setSelectedIndex(-1);
        dropdown2.setSelectedIndex(-1);
    }


    public String getSelectedMerchant() {
        return (String) dropdown1.getSelectedItem();
    }


    public String getSelectedClient() {
        return (String) dropdown2.getSelectedItem();
    }

    /** Enables or disables the start trading button. */
    public void setStartTradingEnabled(boolean enabled) {
        btnStartTrading.setEnabled(enabled);
    }

}
