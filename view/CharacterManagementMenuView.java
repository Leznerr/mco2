package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * First screen for character management where the user selects
 * which player's roster to manage.
 */
public class CharacterManagementMenuView extends JFrame {

    public static final String MANAGE_PLAYER1 = "Manage Player 1";
    public static final String MANAGE_PLAYER2 = "Manage Player 2";
    public static final String RETURN_TO_MENU = "Return to Menu";

    private final JButton btnManagePlayer1 = new RoundedButton(MANAGE_PLAYER1);
    private final JButton btnManagePlayer2 = new RoundedButton(MANAGE_PLAYER2);
    private final JButton btnReturnToMenu = new RoundedButton(RETURN_TO_MENU);

    public CharacterManagementMenuView() {
        super("Character Management");
        initUI();
        setSize(800, 700);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initUI() {
        JPanel bgPanel = new JPanel() {
            private final Image bg = new ImageIcon("view/assets/CharAndPlayerCharManagBG.jpg").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth(), h = getHeight();
                double s = Math.max(w / (double) bg.getWidth(null), h / (double) bg.getHeight(null));
                int ww = (int) (bg.getWidth(null) * s);
                int hh = (int) (bg.getHeight(null) * s);
                g.drawImage(bg, (w - ww) / 2, (h - hh) / 2, ww, hh, this);
            }
        };
        bgPanel.setLayout(new BoxLayout(bgPanel, BoxLayout.Y_AXIS));
        bgPanel.add(Box.createVerticalStrut(80));

        ImageIcon logoIcon = new ImageIcon("view/assets/CharManagLogo.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(500, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bgPanel.add(logoLabel);
        bgPanel.add(Box.createVerticalStrut(60));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(btnManagePlayer1);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(btnManagePlayer2);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(btnReturnToMenu);
        bgPanel.add(buttonPanel);
        bgPanel.add(Box.createVerticalGlue());

        setContentPane(bgPanel);
    }

    public void setActionListener(ActionListener l) {
        btnManagePlayer1.addActionListener(l);
        btnManagePlayer2.addActionListener(l);
        btnReturnToMenu.addActionListener(l);
    }
}
