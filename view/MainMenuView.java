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

// import controller.____;

/**
 * The main menu view for Fatal Fantasy: Tactics Game.
 */
public class MainMenuView extends JPanel {
    // Button labels
    public static final String REGISTER_PLAYERS = "Register Players";
    public static final String MANAGE_CHARACTERS = "Manage Characters";
    public static final String HALL_OF_FAME = "Hall Of Fame";
    public static final String START_BATTLE = "Start Battle";
    public static final String EXIT = "Exit";

    // UI components
    private JButton btnRegisterPlayers;
    private JButton btnManageCharacters;
    private JButton btnHallOfFame;
    private JButton btnStartBattle;
    private JButton btnExit;


    /**
     * Constructs the Main Menu UI of Fatal Fantasy: Tactics Game.
     */
    public MainMenuView() {

        initUI();
        


    }


    /**
     * Initializes the UI components and arranges them in the main layout.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private Image bg = new ImageIcon("view/assets/MainMenuBG.jpg").getImage();

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
        backgroundPanel.add(Box.createVerticalStrut(40));

        // Logo image centered and scaled
        ImageIcon logoIcon = new ImageIcon("view/assets/MainLogoBG2.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(300, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(logoLabel);

        // Add vertical space between logo and buttons
        backgroundPanel.add(Box.createVerticalStrut(40));

        // Panel for buttons, centered
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        btnRegisterPlayers = new RoundedButton(REGISTER_PLAYERS);
        btnManageCharacters = new RoundedButton(MANAGE_CHARACTERS);
        btnHallOfFame = new RoundedButton(HALL_OF_FAME);
        btnStartBattle = new RoundedButton(START_BATTLE);
        btnExit = new RoundedButton(EXIT);

        // Add buttons with vertical spacing (how they stack)
        buttonPanel.add(btnRegisterPlayers);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnManageCharacters);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnHallOfFame);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnStartBattle);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnExit);

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
        btnRegisterPlayers.addActionListener(listener);
        btnManageCharacters.addActionListener(listener);
        btnHallOfFame.addActionListener(listener);
        btnStartBattle.addActionListener(listener);
        btnExit.addActionListener(listener);
    }
    
}