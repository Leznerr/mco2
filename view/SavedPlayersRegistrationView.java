package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// import controller._;

/**
 * The saved players registration view for Fatal Fantasy: Tactics Game.
 */
public class SavedPlayersRegistrationView extends JFrame {
    // Button labels
    public static final String REGISTER = "Register";
    public static final String RETURN = "Return";

    // UI components
    private JComboBox<String> dropdown1 = new JComboBox<>();
    private JComboBox<String> dropdown2 = new JComboBox<>();
    private JButton btnRegister;
    private JButton btnReturn;
    
    
    /**
     * Constructs the SavedPlayersRegistrationView UI of Fatal Fantasy: Tactics Game.
     */
    public SavedPlayersRegistrationView() {
        super("Fatal Fantasy: Tactics | Saved Players Registration");

        initUI();
        
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    SavedPlayersRegistrationView.this,
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
            private Image bg = new ImageIcon("view/assets/PlayersRegistrationBG.jpg").getImage();
            
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
        ImageIcon logoIcon = new ImageIcon("view/assets/SavedPlayersRegLogo.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(500, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        
        centerPanel.add(Box.createVerticalStrut(20));

        // Dropdown Panels with Outlined Labels
        centerPanel.add(createDropdownPanel("Select Player 1:      ", dropdown1));
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(createDropdownPanel("Select Player 2:     ", dropdown2));

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 50));
        buttonPanel.setOpaque(false);

        btnRegister = new RoundedButton(REGISTER);
        btnReturn = new RoundedButton(RETURN);

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnReturn);

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

    /**
     * Sets the action listener for the button click events.
     * 
     * @param listener The listener
     */
    public void setActionListener(ActionListener listener) {
        btnRegister.addActionListener(listener);
        btnReturn.addActionListener(listener);
        dropdown1.addActionListener(listener);
        dropdown2.addActionListener(listener);
    }


    /**
     * Sets the available players in the dropdown.
     * 
     * @param options The array of player options
     */
    public void setPlayer1Options(String[] options) {
        dropdown1.removeAllItems();
        for (String opt : options) dropdown1.addItem(opt);
    }


    /**
     * Sets the available players in the dropdown.
     * 
     * @param options The array of player options
     */
    public void setPlayer2Options(String[] options) {
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


    public String getSelectedPlayer1() {
        return (String) dropdown1.getSelectedItem();
    }


    public String getSelectedPlayer2() {
        return (String) dropdown2.getSelectedItem();
    }

}