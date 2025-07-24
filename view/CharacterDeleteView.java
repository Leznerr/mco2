package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * The character delete view for Fatal Fantasy: Tactics Game.
 * <p>This class is responsible for rendering the UI that allows
 * a player to select and delete one of their characters.</p>
 */
public class CharacterDeleteView extends JFrame {
    private final int playerID;

    // Button labels
    public static final String DELETE = "Delete";
    public static final String RETURN = "Return";

    // UI components
    private final JButton btnDelete;
    private final JButton btnReturn;
    private final JComboBox<String> charDropdown;
    private final JTextArea charListArea;

    /**
     * Constructs the Specific Character Deletion UI of Fatal Fantasy: Tactics Game.
     *
     * @param playerID The ID of the player viewing the screen
     */
    public CharacterDeleteView(int playerID) {
        super("Fatal Fantasy: Tactics | Player " + playerID + " Character Deletion");
        this.playerID = playerID;

        btnDelete = new RoundedButton(DELETE);
        btnReturn = new RoundedButton(RETURN);
        charDropdown = new JComboBox<>();
        charListArea = new JTextArea();

        initUI();
        configureWindow();
    }

    private void configureWindow() {
        setSize(800, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        CharacterDeleteView.this,
                        "Are you sure you want to quit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });


    }

    /**
     * Initializes and lays out all UI components.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private final Image bg = new ImageIcon("view/assets/DeleteCharBG.jpg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth(), h = getHeight();
                double scale = Math.max(w / (double) bg.getWidth(null), h / (double) bg.getHeight(null));
                int ww = (int) (bg.getWidth(null) * scale);
                int hh = (int) (bg.getHeight(null) * scale);
                g.drawImage(bg, (w - ww) / 2, (h - hh) / 2, ww, hh, this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalStrut(30));

        String headlineImagePath = String.format("view/assets/Player%dCharDeleteLogo.png", playerID);
        ImageIcon logoIcon = new ImageIcon(
                new ImageIcon(headlineImagePath).getImage().getScaledInstance(480, -1, Image.SCALE_SMOOTH));
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);
        centerPanel.add(Box.createVerticalStrut(10));

        RoundedDisplayBox detailsPanel = new RoundedDisplayBox();
        detailsPanel.setPreferredSize(new Dimension(400, 500));
        detailsPanel.setMaximumSize(new Dimension(400, 500));
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        charListArea.setFont(new Font("Serif", Font.PLAIN, 18));
        charListArea.setForeground(Color.WHITE);
        charListArea.setOpaque(false);
        charListArea.setEditable(false);
        charListArea.setLineWrap(true);
        charListArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(charListArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        detailsPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(detailsPanel);
        centerPanel.add(Box.createVerticalStrut(40));

        JLabel selectLabel = new JLabel("Select a Character:");
        selectLabel.setFont(new Font("Serif", Font.BOLD, 18));
        selectLabel.setForeground(Color.WHITE);
        selectLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JPanel dropdownPanel = new JPanel();
        dropdownPanel.setOpaque(false);
        dropdownPanel.setLayout(new BoxLayout(dropdownPanel, BoxLayout.X_AXIS));
        dropdownPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dropdownPanel.add(selectLabel);

        charDropdown.setFont(new Font("Serif", Font.BOLD, 18));
        charDropdown.setMaximumSize(new Dimension(250, 35));
        charDropdown.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        dropdownPanel.add(charDropdown);

        centerPanel.add(dropdownPanel);
        centerPanel.add(Box.createVerticalGlue());

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 50));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnReturn);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(backgroundPanel);
    }

    // ─── Event Registration & Helpers ────────────────────────────────────────────

    public void setActionListener(ActionListener listener) {
        btnReturn.addActionListener(listener);
        btnDelete.addActionListener(listener);
        charDropdown.addActionListener(listener);
    }

    public void showInfoMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean confirmCharacterDeletion(String characterName) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete \"" + characterName + "\"?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);
        return option == JOptionPane.YES_OPTION;
    }

    public void updateCharacterList(String text) {
        charListArea.setText(text);
    }

    public void setCharacterOptions(String[] characterNames) {
        charDropdown.removeAllItems();
        for (String name : characterNames) {
            charDropdown.addItem(name);
        }
    }

    public String getSelectedCharacter() {
        return (String) charDropdown.getSelectedItem();
    }

    public JButton getDeleteButton() {
        return btnDelete;
    }

    public JButton getReturnButton() {
        return btnReturn;
    }
}
