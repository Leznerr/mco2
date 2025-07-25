package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
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

/**
 * The player registration view for Fatal Fantasy: Tactics Game.
 * <p>
 * Allows user to enter names for Player 1 and Player 2.
 * All GUI logic is decoupled from game logic per MVC.
 */
public class PlayerRegistrationView extends JFrame {
    // Button labels
    public static final String REGISTER = "Register";
    public static final String RETURN = "Return";

    private RoundedTextField player1Field;
    private RoundedTextField player2Field;
    private JButton btnRegister;
    private JButton btnReturn;
    private ActionListener externalListener;

    /**
     * Constructs the Player Registration screen.
     */
    public PlayerRegistrationView() {
        super("Fatal Fantasy: Tactics | Player Registration");
        initUI();

        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    PlayerRegistrationView.this,
                    "Are you sure you want to quit?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION && externalListener != null) {
                    ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Exit");
                    externalListener.actionPerformed(evt);
                }
            }
        });

        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Initializes and arranges all UI components.
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
                double scale = Math.max(panelWidth / (double) imgWidth, panelHeight / (double) imgHeight);
                int width = (int) (imgWidth * scale);
                int height = (int) (imgHeight * scale);
                int x = (panelWidth - width) / 2;
                int y = (panelHeight - height) / 2;
                g.drawImage(bg, x, y, width, height, this);
            }
        };

        backgroundPanel.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        centerPanel.add(Box.createVerticalStrut(100));
        ImageIcon logoIcon = new ImageIcon("view/assets/PlayerRegLogo.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(400, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);
        centerPanel.add(Box.createVerticalStrut(40));

        player1Field = new RoundedTextField("Enter name for Player 1", 20);
        player1Field.setMaximumSize(new Dimension(300, 45));
        centerPanel.add(player1Field);
        centerPanel.add(Box.createVerticalStrut(20));

        player2Field = new RoundedTextField("Enter name for Player 2", 20);
        player2Field.setMaximumSize(new Dimension(300, 45));
        centerPanel.add(player2Field);

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

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
     * Registers an action listener to all interactive buttons and window exit.
     *
     * @param listener The controller-provided listener
     */
    public void setActionListener(ActionListener listener) {
        this.externalListener = listener;
        btnRegister.addActionListener(listener);
        btnReturn.addActionListener(listener);
    }

    /**
     * Returns trimmed Player 1 name input.
     *
     * @return Player 1 name
     */
    public String getPlayer1Name() {
        return player1Field.getText().trim();
    }

    /**
     * Returns trimmed Player 2 name input.
     *
     * @return Player 2 name
     */
    public String getPlayer2Name() {
        return player2Field.getText().trim();
    }

     /** Dialog helpers used by the controller. */
    public void showInfoMessage(String msg)  {
        JOptionPane.showMessageDialog(this, msg, "Info",  JOptionPane.INFORMATION_MESSAGE);
    }   
    public void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
 * Clears both player-name text fields.
 * <p>Called by SceneManager each time the “Register Players” card is shown
 * so the user always starts with blank inputs.</p>
 */
public void resetFields() {
    player1Field.setText("");
    player2Field.setText("");
}
}
