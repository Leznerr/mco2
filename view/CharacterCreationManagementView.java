package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * The character creation management menu view for Fatal Fantasy: Tactics Game.
 * <p>
 * Now supports playerName (String) as the unique identifier, not playerID.
 * </p>
 */
public class CharacterCreationManagementView extends JFrame {
    private final String playerName;

    // Button labels
    public static final String MANUAL_CREATION = "Manual Creation";
    public static final String AUTO_CREATION = "Auto Creation";
    public static final String RETURN = "Return";

    // UI components
    private JButton btnManualCreation;
    private JButton btnAutoCreation;
    private JButton btnReturn;

    /**
     * Constructs the Character Creation Management UI for the given player.
     * @param playerName the unique player name
     */
    public CharacterCreationManagementView(String playerName) {
        super("Fatal Fantasy: Tactics | " + playerName + " Character Creation Modes");
        this.playerName = playerName;

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
                    dispose();
                }
            }
        });

        setLocationRelativeTo(null);
        setResizable(false);
    }

    /** Dialog helpers used by the controller. */
    public void showInfoMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    public void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Initializes the UI components and arranges them in the main layout.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private final Image bg = new ImageIcon("view/assets/CharCreationBG.jpg").getImage();
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
        backgroundPanel.add(Box.createVerticalStrut(60));

        // Logo image centered and scaled (use player name in asset path or as subtitle as needed)
        String logoPath = String.format("view/assets/%sCharCreationModesLogo.png", playerName);
        ImageIcon logoIcon = new ImageIcon(logoPath);
        Image logoImg = logoIcon.getImage().getScaledInstance(500, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(logoLabel);

        backgroundPanel.add(Box.createVerticalStrut(70));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        btnManualCreation = new RoundedButton(MANUAL_CREATION);
        btnAutoCreation = new RoundedButton(AUTO_CREATION);
        btnReturn = new RoundedButton(RETURN);

        buttonPanel.add(btnManualCreation);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnAutoCreation);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnReturn);

        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(buttonPanel);
        backgroundPanel.add(Box.createVerticalGlue());

        setContentPane(backgroundPanel);
    }

    /**
     * Sets the action listener for the button click events.
     * @param listener The listener
     */
    public void setActionListener(ActionListener listener) {
        btnManualCreation.addActionListener(listener);
        btnAutoCreation.addActionListener(listener);
        btnReturn.addActionListener(listener);
    }

    /**
     * Returns the player name this view is bound to.
     */
    public String getPlayerName() {
        return playerName;
    }
}
