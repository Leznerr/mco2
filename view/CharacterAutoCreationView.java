package view;

import controller.CharacterAutoCreationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Swing view that lets a player auto-generate a character.
 *
 * <p>Pure view: renders UI, exposes listener registration and setter
 * methods—no game logic inside.</p>
 */
public final class CharacterAutoCreationView extends JFrame {

    private final String playerName;

    public static final String RANDOMIZE = "RANDOMIZE";
    public static final String CREATE    = "CREATE";
    public static final String RETURN    = "RETURN";

    private CharacterAutoCreationController controller;

    private final RoundedTextField nameField;
    private final JTextArea charDetailsArea;
    private final JButton btnRandomize;
    private final JButton btnCreate;
    private final JButton btnReturn;

    /**
     * Constructs the CharacterAutoCreationView for a specific player.
     *
     * @param playerName the name of the current player
     */
    public CharacterAutoCreationView(String playerName) {
        super("Fatal Fantasy: Tactics | Player " + playerName + " Auto Character Creation");
        this.playerName = playerName;

        nameField        = new RoundedTextField("Enter character name", 20);
        charDetailsArea  = new JTextArea();
        btnRandomize     = new RoundedButton(RANDOMIZE);
        btnCreate        = new RoundedButton(CREATE);
        btnReturn        = new RoundedButton(RETURN);

        initUI();
        configureFrame();
    }

    private void configureFrame() {
        setSize(800, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opt = JOptionPane.showConfirmDialog(
                        CharacterAutoCreationView.this,
                        "Are you sure you want to quit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) dispose();
            }
        });

    }

    private void initUI() {
        JPanel bgPanel = new JPanel() {
            private final Image bg = new ImageIcon("view/assets/CharCreationBG.jpg").getImage();
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth(), h = getHeight();
                double scale = Math.max(w / (double) bg.getWidth(null),
                                        h / (double) bg.getHeight(null));
                int ww = (int) (bg.getWidth(null) * scale);
                int hh = (int) (bg.getHeight(null) * scale);
                g.drawImage(bg, (w - ww) / 2, (h - hh) / 2, ww, hh, this);
            }
        };
        bgPanel.setLayout(new BorderLayout());

        JPanel centre = new JPanel();
        centre.setOpaque(false);
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.add(Box.createVerticalStrut(40));

        String logoPath = "view/assets/Player" + playerName + "AutoCharCreationLogo.png";
        ImageIcon logoIcon = new ImageIcon(
                new ImageIcon(logoPath).getImage().getScaledInstance(550, -1, Image.SCALE_SMOOTH));
        JLabel logo = new JLabel(logoIcon);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(logo);
        centre.add(Box.createVerticalStrut(10));

        nameField.setMaximumSize(new Dimension(300, 35));
        centre.add(nameField);
        centre.add(Box.createVerticalStrut(20));

        RoundedDisplayBox detailsBox = new RoundedDisplayBox();
        detailsBox.setPreferredSize(new Dimension(400, 500));
        detailsBox.setMaximumSize(new Dimension(400, 500));
        detailsBox.setLayout(new BorderLayout());
        detailsBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        charDetailsArea.setFont(new Font("Serif", Font.PLAIN, 18));
        charDetailsArea.setForeground(Color.WHITE);
        charDetailsArea.setOpaque(false);
        charDetailsArea.setEditable(false);
        charDetailsArea.setLineWrap(true);
        charDetailsArea.setWrapStyleWord(true);

        JScrollPane scroller = new JScrollPane(charDetailsArea);
        scroller.setOpaque(false);
        scroller.getViewport().setOpaque(false);
        scroller.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        detailsBox.add(scroller, BorderLayout.CENTER);
        centre.add(detailsBox);
        centre.add(Box.createVerticalGlue());

        bgPanel.add(centre, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 50));
        buttons.setOpaque(false);
        buttons.add(btnRandomize);
        buttons.add(btnCreate);
        buttons.add(btnReturn);
        bgPanel.add(buttons, BorderLayout.SOUTH);

        setContentPane(bgPanel);
    }

    public void addActionListener(ActionListener l) {
        btnRandomize.setActionCommand(RANDOMIZE);
        btnCreate.setActionCommand(CREATE);
        btnReturn.setActionCommand(RETURN);

        btnRandomize.addActionListener(l);
        btnCreate.addActionListener(l);
        btnReturn.addActionListener(l);
    }

    public boolean confirmCharacterCreation(String charName) {
        int opt = JOptionPane.showConfirmDialog(
                this,
                "Create \"" + charName + "\"?",
                "Confirm Creation",
                JOptionPane.YES_NO_OPTION);
        return opt == JOptionPane.YES_OPTION;
    }

    public void showGeneratedDetails(String details) {
        charDetailsArea.setText(details);
    }

    public void showInfoMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void resetFields() {
        nameField.setText("");
        charDetailsArea.setText("");
    }

    public String getCharacterName() {
        return nameField.getText().trim();
    }

    public String getGeneratedCharacterDetails() {
        return charDetailsArea.getText().trim();
    }

    public JButton getRandomizeButton() { return btnRandomize; }
    public JButton getCreateButton()    { return btnCreate;    }
    public JButton getReturnButton()    { return btnReturn;    }

    public void setController(CharacterAutoCreationController controller) {
        this.controller = controller;
    }
}
