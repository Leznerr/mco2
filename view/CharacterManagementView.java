package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// import controller._;

/**
 * The character management menu view for Fatal Fantasy: Tactics Game.
 */
public class CharacterManagementView extends JFrame {
    // Button labels
    public static final String MANAGE_PLAYER1 = "Manage Player 1";
    public static final String MANAGE_PLAYER2 = "Manage Player 2";
    public static final String RETURN_TO_MENU = "Return to Menu";

    // UI components
    private JButton btnManagePlayer1;
    private JButton btnManagePlayer2;
    private JButton btnReturnToMenu;
    
    
    /**
     * Constructs the Character Management UI of Fatal Fantasy: Tactics Game.
     */
    public CharacterManagementView() {
        super("Fatal Fantasy: Tactics | Character Management");

        initUI();
        
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    CharacterManagementView.this,
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
            private Image bg = new ImageIcon("view/assets/CharAndPlayerCharManagBG.jpg").getImage();

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
        backgroundPanel.add(Box.createVerticalStrut(100));

        // Logo image centered and scaled
        ImageIcon logoIcon = new ImageIcon("view/assets/CharManagLogo.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(500, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(logoLabel);

        // Add vertical space between logo and buttons
        backgroundPanel.add(Box.createVerticalStrut(60));

        // Panel for buttons, centered
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        btnManagePlayer1 = new RoundedButton(MANAGE_PLAYER1);
        btnManagePlayer2 = new RoundedButton(MANAGE_PLAYER2);
        btnReturnToMenu = new RoundedButton(RETURN_TO_MENU);

        // Add buttons with vertical spacing (how they stack)
        buttonPanel.add(btnManagePlayer1);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnManagePlayer2);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnReturnToMenu);

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
        btnManagePlayer1.addActionListener(listener);
        btnManagePlayer2.addActionListener(listener);
        btnReturnToMenu.addActionListener(listener);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void displayCharacterList(java.util.List<String> summaries) {
        // no-op placeholder for legacy controller compatibility
    }

}