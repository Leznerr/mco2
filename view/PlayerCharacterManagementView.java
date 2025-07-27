package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Per-player character management menu. Lets the user view, create,
 * edit or delete characters for a specific player.
 */
public class PlayerCharacterManagementView extends JFrame {

    public static final String VIEW_CHARACTERS = "View Characters";
    public static final String CREATE_CHARACTER = "Create Character";
    public static final String EDIT_CHARACTER = "Edit Character";
    public static final String DELETE_CHARACTER = "Delete Character";
    public static final String INVENTORY = "Inventory";
    public static final String RETURN = "Return";

    private final JButton btnViewCharacters = new RoundedButton(VIEW_CHARACTERS);
    private final JButton btnCreateCharacter = new RoundedButton(CREATE_CHARACTER);
    private final JButton btnEditCharacter = new RoundedButton(EDIT_CHARACTER);
    private final JButton btnDeleteCharacter = new RoundedButton(DELETE_CHARACTER);
    private final JButton btnInventory = new RoundedButton(INVENTORY);
    private final JButton btnReturn = new RoundedButton(RETURN);

    private final int playerID;

    public PlayerCharacterManagementView(int playerID) {
        super("Manage Characters - Player " + playerID);
        this.playerID = playerID;
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
        bgPanel.add(Box.createVerticalStrut(60));

        String logoPath = String.format("view/assets/Player%dCharManagLogo.png", playerID);
        ImageIcon logoIcon = new ImageIcon(logoPath);
        Image logoImg = logoIcon.getImage().getScaledInstance(500, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bgPanel.add(logoLabel);
        bgPanel.add(Box.createVerticalStrut(40));

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
        buttonPanel.add(btnInventory);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(btnReturn);
        bgPanel.add(buttonPanel);
        bgPanel.add(Box.createVerticalGlue());

        setContentPane(bgPanel);
    }

    public void setActionListener(ActionListener l) {
        btnViewCharacters.setActionCommand(VIEW_CHARACTERS);
        btnCreateCharacter.setActionCommand(CREATE_CHARACTER);
        btnEditCharacter.setActionCommand(EDIT_CHARACTER);
        btnDeleteCharacter.setActionCommand(DELETE_CHARACTER);
        btnInventory.setActionCommand(INVENTORY);
        btnReturn.setActionCommand(RETURN);

        btnViewCharacters.addActionListener(l);
        btnCreateCharacter.addActionListener(l);
        btnEditCharacter.addActionListener(l);
        btnDeleteCharacter.addActionListener(l);
        btnInventory.addActionListener(l);
        btnReturn.addActionListener(l);
    }

    public int getPlayerID() { return playerID; }
}
