package view;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

// import controller._;

/**
 * The hall of fame management view for Fatal Fantasy: Tactics Game.
 */
public class HallOfFameManagementView extends JPanel {
    // Button labels
    public static final String TOP_PLAYERS = "Top Players";
    public static final String TOP_CHARACTERS = "Top Characters";
    public static final String RETURN = "Return to Menu";

    // UI components
    private JButton btnTopPlayers;
    private JButton btnTopCharacters;
    private JButton btnReturn;
    
    
    /**
     * Constructs the Hall Of Fame Management UI of Fatal Fantasy: Tactics Game.
     */
    public HallOfFameManagementView() {

        initUI();
        


    }


    /**
     * Initializes the UI components and arranges them in the main layout.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private Image bg = new ImageIcon("view/assets/HallOfFameBG.jpg").getImage();

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
        
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));

        // Add vertical space at the top
        backgroundPanel.add(Box.createVerticalStrut(60));

        // Logo image centered and scaled
        ImageIcon logoIcon = new ImageIcon("view/assets/HallOfFameManagLogo.png");        
        Image logoImg = logoIcon.getImage().getScaledInstance(650, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(logoLabel);

        // Add vertical space between logo and buttons
        backgroundPanel.add(Box.createVerticalStrut(100));

        // Panel for buttons, centered
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        btnTopPlayers = new RoundedButton(TOP_PLAYERS);
        btnTopCharacters = new RoundedButton(TOP_CHARACTERS);
        btnReturn = new RoundedButton(RETURN);

        // Add buttons with vertical spacing (how they stack)
        buttonPanel.add(btnTopPlayers);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnTopCharacters);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnReturn);

        // Center the button panel horizontally
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(buttonPanel);

        // Add vertical glue to push everything to the center
        backgroundPanel.add(Box.createVerticalGlue());

        setLayout(new BorderLayout());
        add(backgroundPanel);
    }


    /**
     * Sets the action listener for the button click events.
     * 
     * @param listener The listener
     */
    public void setActionListener(ActionListener listener) {
        btnTopPlayers.addActionListener(listener);
        btnTopCharacters.addActionListener(listener);
        btnReturn.addActionListener(listener);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

}