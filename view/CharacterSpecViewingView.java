package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// import controller._;

/**
 * The character specific view for Fatal Fantasy: Tactics Game.
 */
public class CharacterSpecViewingView extends JFrame {
    private int playerID;

    // Button labels
    public static final String RETURN = "Return";

    // UI components
    private JButton btnReturn;
    private JComboBox<String> charDropdown;
    private JTextArea charDetailsArea;


    /**
     * Constructs the Specific Character Viewing UI of Fatal Fantasy: Tactics Game.
     */
    public CharacterSpecViewingView(int playerID) {
        super("Fatal Fantasy: Tactics | Player " + playerID + " Character Specific Viewing");

        this.playerID = playerID;

        initUI();
        
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    CharacterSpecViewingView.this,
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
    }


    /**
     * Initializes the UI components and arranges them in the main layout.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            String headlineImagePath = String.format("view/assets/ViewAChar%dBG.jpg", playerID);
            private Image bg = new ImageIcon(headlineImagePath).getImage();

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

        // Center panel for headline + display box
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalStrut(30));

        String headlineImagePath = String.format("view/assets/Player%dCharSpecViewLogo.png", playerID);
        ImageIcon logoIcon = new ImageIcon(headlineImagePath);
        Image logoImg = logoIcon.getImage().getScaledInstance(450, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);

        centerPanel.add(Box.createVerticalStrut(10));

        charDropdown = new JComboBox<>();
        centerPanel.add(createDropdownPanel("Select a Character:", charDropdown));

        centerPanel.add(Box.createVerticalStrut(20));

        // Rounded display box for character list
        RoundedDisplayBox detailsPanel = new RoundedDisplayBox();
        detailsPanel.setPreferredSize(new Dimension(400, 500));
        detailsPanel.setMaximumSize(new Dimension(400, 500));
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Text area for character list
        charDetailsArea = new JTextArea();
        charDetailsArea.setFont(new Font("Serif", Font.PLAIN, 18));
        charDetailsArea.setForeground(Color.WHITE);
        charDetailsArea.setOpaque(false);
        charDetailsArea.setEditable(false);
        charDetailsArea.setLineWrap(true);
        charDetailsArea.setWrapStyleWord(true);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(charDetailsArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        detailsPanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(detailsPanel);
        centerPanel.add(Box.createVerticalGlue());

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 50));
        buttonPanel.setOpaque(false);

        btnReturn = new RoundedButton(RETURN);

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
        btnReturn.addActionListener(listener);
        charDropdown.addActionListener(listener);
    }


    /**
     * Updates the character list display area.
     * 
     * @param text The formatted character list string
     */
    public void updateCharacterDetails(String text) {
        charDetailsArea.setText(text);
    }


    /**
     * Sets the character options in the dropdown.
     * 
     * @param characterNames The array of character names
     */
    public void setCharacterOptions(String[] characterNames) {
        charDropdown.removeAllItems();
        for (String name : characterNames) {
            charDropdown.addItem(name);
        }
    }

    /**
     * Enables or disables the dropdown used for character selection.
     *
     * @param enabled true to enable selection, false to disable
     */
    public void setCharacterSelectionEnabled(boolean enabled) {
        charDropdown.setEnabled(enabled);
    }


    public String getSelectedCharacter() {
        return (String) charDropdown.getSelectedItem();
    }


    /**
 * Displays an informational message.
 * @param message the message to show
 */
public void showInfoMessage(String message) {
    JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
}

/**
 * Displays an error message.
 * @param message the message to show
 */
public void showErrorMessage(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
}

/**
 * Clears the character details area and resets dropdown selection.
 */
public void resetView() {
    charDropdown.setSelectedIndex(-1);
    charDetailsArea.setText("");
}

private JPanel createDropdownPanel(String labelText, JComboBox<String> dropdown) {
    JPanel dropdownPanel = new JPanel();
    dropdownPanel.setOpaque(false);
    dropdownPanel.setLayout(new BoxLayout(dropdownPanel, BoxLayout.X_AXIS));
    dropdownPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

    OutlinedLabel label = new OutlinedLabel(labelText);

    int fixedWidth = 200;
    Dimension labelSize = new Dimension(fixedWidth, label.getPreferredSize().height);
    label.setPreferredSize(labelSize);
    label.setMinimumSize(labelSize);
    label.setMaximumSize(labelSize);

    label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

    dropdown.setFont(new Font("Serif", Font.BOLD, 18));
    dropdown.setForeground(Color.WHITE);
    dropdown.setRenderer(new WhiteTextCellRenderer());
    dropdown.setMaximumSize(new Dimension(250, 35));
    dropdown.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

    dropdownPanel.add(label);
    dropdownPanel.add(dropdown);

    return dropdownPanel;
}
}
