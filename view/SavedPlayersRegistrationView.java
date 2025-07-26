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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

// import controller._;

/**
 * The saved players registration view for Fatal Fantasy: Tactics Game.
 */
public class SavedPlayersRegistrationView extends JPanel {
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

        initUI();
        


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

        setLayout(new BorderLayout());
        add(backgroundPanel);
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