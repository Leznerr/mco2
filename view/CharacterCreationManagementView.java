package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// import controller._;

/**
 * The character creation management menu view for Fatal Fantasy: Tactics Game.
 */
public class CharacterCreationManagementView extends JFrame {
    private int playerID;

    // Button labels
    public static final String MANUAL_CREATION = "Manual Creation";
    public static final String AUTO_CREATION = "Auto Creation";
    public static final String RETURN = "Return";

    // UI components
    private JButton btnManualCreation;
    private JButton btnAutoCreation;
    private JButton btnReturn;
    
    
    /**
     * Constructs the Character Creation Management UI of Fatal Fantasy: Tactics Game.
     */
    public CharacterCreationManagementView(int playerID) {
        super("Fatal Fantasy: Tactics | Player " + playerID + " Character Creation Modes");

        this.playerID = playerID;

        initUI();
        
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    CharacterCreationManagementView.this,
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
        setVisible(true);
    }


    /**
     * Initializes the UI components and arranges them in the main layout.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private Image bg = new ImageIcon("view/assets/CharCreationBG.jpg").getImage();

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
        String headlineImagePath = String.format("view/assets/Player%dCharCreationModesLogo.png", playerID);
        ImageIcon logoIcon = new ImageIcon(headlineImagePath);        
        Image logoImg = logoIcon.getImage().getScaledInstance(500, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(logoLabel);

        // Add vertical space between logo and buttons
        backgroundPanel.add(Box.createVerticalStrut(70));

        // Panel for buttons, centered
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        btnManualCreation = new RoundedButton(MANUAL_CREATION);
        btnAutoCreation = new RoundedButton(AUTO_CREATION);
        btnReturn = new RoundedButton(RETURN);

        // Add buttons with vertical spacing (how they stack)
        buttonPanel.add(btnManualCreation);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnAutoCreation);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnReturn);

        // Center the button panel horizontally
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(buttonPanel);

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
        btnManualCreation.addActionListener(listener);
        btnAutoCreation.addActionListener(listener);
        btnReturn.addActionListener(listener);
    }

}