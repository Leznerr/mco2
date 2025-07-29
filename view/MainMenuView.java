package view;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * The main menu view for Fatal Fantasy: Tactics Game.
 */
public class MainMenuView extends JFrame {
    // Button labels
    public static final String REGISTER_PLAYERS = "Register Players";
    public static final String MANAGE_CHARACTERS = "Manage Characters";
    public static final String START_BATTLE = "Start Battle";
    public static final String TRADE = "Trading Hall";
    public static final String HALL_OF_FAME = "Hall Of Fame";
    public static final String EXIT = "Exit";

    // Action command constants
    public static final String ACTION_REGISTER_PLAYERS = "ACTION_REGISTER_PLAYERS";
    public static final String ACTION_MANAGE_CHARACTERS = "ACTION_MANAGE_CHARACTERS";
    public static final String ACTION_HALL_OF_FAME = "ACTION_HALL_OF_FAME";
    public static final String ACTION_TRADING_HALL = "ACTION_TRADING_HALL";
    public static final String ACTION_START_BATTLE = "ACTION_START_BATTLE";
    public static final String ACTION_EXIT = "ACTION_EXIT";

    // UI components
    private JButton btnRegisterPlayers;
    private JButton btnManageCharacters;
    private JButton btnStartBattle;
    private JButton btnTrade;
    private JButton btnHallOfFame;
    private JButton btnExit;


    /**
     * Constructs the Main Menu UI of Fatal Fantasy: Tactics Game.
     */
    public MainMenuView() {
        super("Fatal Fantasy: Tactics | Main Menu");

        initUI();
        
        setSize(800, 700);
        // Use DO_NOTHING_ON_CLOSE so GameManagerController can handle
        // confirmation and graceful shutdown.
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        setLocationRelativeTo(null);
        setResizable(false);

        // Dispose this frame when the user clicks the window close button so
        // the application can terminate naturally when no windows remain.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        setVisible(true);
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
        backgroundPanel.add(Box.createVerticalStrut(30));

        // Logo image centered and scaled
        ImageIcon logoIcon = new ImageIcon("view/assets/MainLogoBG2.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(300, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(logoLabel);

        // Add vertical space between logo and buttons
        backgroundPanel.add(Box.createVerticalStrut(20));

        // Panel for buttons, centered within a wrapper
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        btnRegisterPlayers = new RoundedButton(REGISTER_PLAYERS);
        btnManageCharacters = new RoundedButton(MANAGE_CHARACTERS);
        btnStartBattle = new RoundedButton(START_BATTLE);
        btnTrade = new RoundedButton(TRADE);
        btnHallOfFame = new RoundedButton(HALL_OF_FAME);
        btnExit = new RoundedButton(EXIT);

        btnRegisterPlayers.setActionCommand(ACTION_REGISTER_PLAYERS);
        btnManageCharacters.setActionCommand(ACTION_MANAGE_CHARACTERS);
        btnHallOfFame.setActionCommand(ACTION_HALL_OF_FAME);
        btnTrade.setActionCommand(ACTION_TRADING_HALL);
        btnStartBattle.setActionCommand(ACTION_START_BATTLE);
        btnExit.setActionCommand(ACTION_EXIT);

        // Add buttons with consistent vertical spacing
        buttonPanel.add(btnRegisterPlayers);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnManageCharacters);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnStartBattle);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnTrade);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnHallOfFame);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnExit);

        // Wrap the button panel in a centered container
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(buttonPanel);
        backgroundPanel.add(wrapper);

        // Add vertical glue to push everything to the center
        backgroundPanel.add(Box.createVerticalGlue());

        setContentPane(backgroundPanel);
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
        btnTrade.addActionListener(listener);
        btnStartBattle.addActionListener(listener);
        btnExit.addActionListener(listener);
    }
    
}
