package view;

import model.core.Player;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Character Management Menu View for Fatal Fantasy: Tactics Game.
 * <p>
 * Solely responsible for GUI rendering and forwarding user actions via listeners.
 */
public class CharacterManagementView extends JFrame {

    // Button labels
    public static final String MANAGE_PLAYER1 = "Manage Player 1";
    public static final String MANAGE_PLAYER2 = "Manage Player 2";
    public static final String RETURN_TO_MENU = "Return to Menu";

    // UI components
    private final JButton btnManagePlayer1;
    private final JButton btnManagePlayer2;
    private final JButton btnReturnToMenu;
    private final JTextArea characterListArea;

    private Player player; // Player object to manage characters
    
    /**
     * Constructs the Character Management UI.
     *
     * @param player the Player object that will be managed.
     */
    public CharacterManagementView(Player player) {
        super("Fatal Fantasy: Tactics | Character Management");
        this.player = player;

        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        CharacterManagementView.this,
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

        // Initialize UI
        JPanel backgroundPanel = new JPanel() {
            private final Image bg = new ImageIcon("view/assets/CharAndPlayerCharManagBG.jpg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int imgWidth = bg.getWidth(this);
                int imgHeight = bg.getHeight(this);
                double scale = Math.max(panelWidth / (double) imgWidth, panelHeight / (double) imgHeight);
                int width = (int) (imgWidth * scale);
                int height = (int) (imgHeight * scale);
                int x = (panelWidth - width) / 2;
                int y = (panelHeight - height) / 2;
                g.drawImage(bg, x, y, width, height, this);
            }
        };

        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));
        backgroundPanel.add(Box.createVerticalStrut(80));

        ImageIcon logoIcon = new ImageIcon("view/assets/CharManagLogo.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(500, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(logoLabel);

        backgroundPanel.add(Box.createVerticalStrut(30));

        // Create and add character list area
        characterListArea = new JTextArea(12, 40);
        characterListArea.setEditable(false);
        characterListArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        JScrollPane scrollPane = new JScrollPane(characterListArea);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        backgroundPanel.add(scrollPane);

        backgroundPanel.add(Box.createVerticalStrut(20));

        // Buttons
        btnManagePlayer1 = new RoundedButton(MANAGE_PLAYER1);
        btnManagePlayer2 = new RoundedButton(MANAGE_PLAYER2);
        btnReturnToMenu = new RoundedButton(RETURN_TO_MENU);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(btnManagePlayer1);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(btnManagePlayer2);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(btnReturnToMenu);

        backgroundPanel.add(buttonPanel);
        backgroundPanel.add(Box.createVerticalGlue());

        setContentPane(backgroundPanel);
        setVisible(true);
    }

    /**
     * Binds listener to "Manage Player 1" button.
     */
    public void addManagePlayer1Listener(ActionListener listener) {
        btnManagePlayer1.addActionListener(listener);
    }

    /**
     * Binds listener to "Manage Player 2" button.
     */
    public void addManagePlayer2Listener(ActionListener listener) {
        btnManagePlayer2.addActionListener(listener);
    }

    /**
     * Binds listener to "Return to Menu" button.
     */
    public void addReturnToMenuListener(ActionListener listener) {
        btnReturnToMenu.addActionListener(listener);
    }

    /**
     * Displays the character list in the text area.
     *
     * @param formattedCharacterDetails list of strings representing characters
     */
    public void displayCharacterList(List<String> formattedCharacterDetails) {
        characterListArea.setText(String.join("\n", formattedCharacterDetails));
    }

    /**
     * Displays an informational message dialog.
     */
    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays an error message dialog.
     */
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Getter for the player object in case we need it externally
    public Player getPlayer() {
        return this.player;
    }
}
