package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;

/**
 * View for registering two players before they can manage characters or battle.
 * <p>Pure GUI with no business logic.</p>
 */
public class PlayerRegistrationView extends JFrame {

    public static final String REGISTER = "REGISTER";
    public static final String RETURN   = "RETURN";

    private final RoundedTextField player1Field;
    private final RoundedTextField player2Field;
    private final JButton btnRegister;
    private final JButton btnReturn;

    public PlayerRegistrationView() {
        super("Fatal Fantasy: Tactics | Player Registration");

        player1Field = new RoundedTextField("Enter Player 1 name", 20);
        player2Field = new RoundedTextField("Enter Player 2 name", 20);
        btnRegister   = new RoundedButton("Register");
        btnReturn     = new RoundedButton("Return");

        initUI();
        configureWindow();
    }

    private void configureWindow() {
        setSize(800, 700);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opt = JOptionPane.showConfirmDialog(
                        PlayerRegistrationView.this,
                        "Are you sure you want to quit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) dispose();
            }
        });
    }

    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private final Image bg = new ImageIcon("view/assets/PlayersRegistrationBG.jpg").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth();
                int h = getHeight();
                double scale = Math.max(w / (double) bg.getWidth(null), h / (double) bg.getHeight(null));
                int ww = (int) (bg.getWidth(null) * scale);
                int hh = (int) (bg.getHeight(null) * scale);
                g.drawImage(bg, (w - ww) / 2, (h - hh) / 2, ww, hh, this);
            }
        };
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));
        backgroundPanel.add(Box.createVerticalStrut(60));

        ImageIcon logoIcon = new ImageIcon("view/assets/PlayerRegLogo.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(500, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(logoLabel);
        backgroundPanel.add(Box.createVerticalStrut(40));

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setOpaque(false);
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        player1Field.setMaximumSize(new Dimension(300, 35));
        player2Field.setMaximumSize(new Dimension(300, 35));

        fieldsPanel.add(player1Field);
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(player2Field);

        backgroundPanel.add(fieldsPanel);
        backgroundPanel.add(Box.createVerticalStrut(30));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 50));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnReturn);
        backgroundPanel.add(buttonPanel);
        backgroundPanel.add(Box.createVerticalGlue());

        setContentPane(backgroundPanel);
    }

    /** Assigns an ActionListener to the buttons. */
    public void setActionListener(ActionListener l) {
        btnRegister.setActionCommand(REGISTER);
        btnReturn.setActionCommand(RETURN);
        btnRegister.addActionListener(l);
        btnReturn.addActionListener(l);
    }

    /** Returns the trimmed name from player 1 text field. */
    public String getPlayer1Name() {
        return player1Field.getText().trim();
    }

    /** Returns the trimmed name from player 2 text field. */
    public String getPlayer2Name() {
        return player2Field.getText().trim();
    }

    /** Clears both text fields. */
    public void resetFields() {
        player1Field.setText("");
        player2Field.setText("");
    }

    /** Convenience dialog helpers. */
    public void showInfoMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
