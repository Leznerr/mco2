package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// import controller._;

/**
 * The new players registration view for Fatal Fantasy: Tactics Game.
 */
public class NewPlayersRegistrationView extends JFrame {
    // Button labels
    public static final String REGISTER = "Register";
    public static final String RETURN = "Return";

    // UI components
    private RoundedTextField player1Field;
    private RoundedTextField player2Field;
    private JButton btnRegister;
    private JButton btnReturn;
    
    
    /**
     * Constructs the NewPlayersRegistrationView UI of Fatal Fantasy: Tactics Game.
     */
    public NewPlayersRegistrationView() {
        super("Fatal Fantasy: Tactics | New Players Registration");

        initUI();
        
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    NewPlayersRegistrationView.this,
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
        ImageIcon logoIcon = new ImageIcon("view/assets/NewPlayersRegLogo.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(500, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        
        centerPanel.add(Box.createVerticalStrut(20));

        player1Field = new RoundedTextField("Enter name for Player 1", 20);
        player1Field.setMaximumSize(new Dimension(300, 45));
        centerPanel.add(player1Field);
        centerPanel.add(Box.createVerticalStrut(20));

        player2Field = new RoundedTextField("Enter name for Player 2", 20);
        player2Field.setMaximumSize(new Dimension(300, 45));
        centerPanel.add(player2Field);

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for buttons
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
     * Sets the action listener for the button click events.
     * 
     * @param listener The listener
     */
    public void setActionListener(ActionListener listener) {
        btnRegister.addActionListener(listener);
        btnReturn.addActionListener(listener);
    }

    /**
     * Clears the player names fields.
     */
    public void resetFields() {
        player1Field.setText("");
        player2Field.setText("");
    }


    public String getPlayer1Name() {
        return player1Field.getText().trim();
    }


    public String getPlayer2Name() {
        return player2Field.getText().trim();
    }

}