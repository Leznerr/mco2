package view;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.core.Player;

/**
 * Character Management Menu View for Fatal Fantasy: Tactics Game.
 * Solely responsible for GUI rendering and forwarding user actions via listeners.
 */
public class CharacterManagementView extends JFrame {

    // Button action commands
    public static final String VIEW_CHARACTERS   = "View Characters";
    public static final String CREATE_CHARACTER  = "Create Character";
    public static final String EDIT_CHARACTER    = "Edit Character";
    public static final String DELETE_CHARACTER  = "Delete Character";
    public static final String RETURN            = "Return";

    // UI components
    private final JButton btnViewCharacters;
    private final JButton btnCreateCharacter;
    private final JButton btnEditCharacter;
    private final JButton btnDeleteCharacter;
    private final JButton btnReturn;
    private final JTextArea characterListArea;

    private Player player;

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

        // Background panel with custom painting
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

        // Character list area
        characterListArea = new JTextArea(12, 40);
        characterListArea.setEditable(false);
        characterListArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        JScrollPane scrollPane = new JScrollPane(characterListArea);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        backgroundPanel.add(scrollPane);

        backgroundPanel.add(Box.createVerticalStrut(20));

        // Instantiate all buttons
        btnViewCharacters   = new RoundedButton(VIEW_CHARACTERS);
        btnCreateCharacter  = new RoundedButton(CREATE_CHARACTER);
        btnEditCharacter    = new RoundedButton(EDIT_CHARACTER);
        btnDeleteCharacter  = new RoundedButton(DELETE_CHARACTER);
        btnReturn           = new RoundedButton(RETURN);

        // Arrange buttons vertically
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(btnViewCharacters);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(btnCreateCharacter);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(btnEditCharacter);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(btnDeleteCharacter);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(btnReturn);

        backgroundPanel.add(buttonPanel);
        backgroundPanel.add(Box.createVerticalGlue());

        setContentPane(backgroundPanel);
        setVisible(true);
    }

    /**
     * Set a single action listener for all management buttons.
     * Action command will be set for each button.
     */
    public void setActionListener(ActionListener l) {
        btnViewCharacters.setActionCommand(VIEW_CHARACTERS);
        btnCreateCharacter.setActionCommand(CREATE_CHARACTER);
        btnEditCharacter.setActionCommand(EDIT_CHARACTER);
        btnDeleteCharacter.setActionCommand(DELETE_CHARACTER);
        btnReturn.setActionCommand(RETURN);
        btnViewCharacters.addActionListener(l);
        btnCreateCharacter.addActionListener(l);
        btnEditCharacter.addActionListener(l);
        btnDeleteCharacter.addActionListener(l);
        btnReturn.addActionListener(l);
    }

    /**
     * Displays the character list in the text area.
     *
     * @param formattedCharacterDetails list of strings representing characters
     */
    public void displayCharacterList(List<String> formattedCharacterDetails) {
        characterListArea.setText(String.join("\n", formattedCharacterDetails));
    }

    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Getter for the player object in case we need it externally
    public Player getPlayer() {
        return this.player;
    }

    // Expose the buttons for controller if fine-grained control needed
    public JButton getBtnViewCharacters()   { return btnViewCharacters; }
    public JButton getBtnCreateCharacter()  { return btnCreateCharacter; }
    public JButton getBtnEditCharacter()    { return btnEditCharacter; }
    public JButton getBtnDeleteCharacter()  { return btnDeleteCharacter; }
    public JButton getBtnReturn()           { return btnReturn; }
}
