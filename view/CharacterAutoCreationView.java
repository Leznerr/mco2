package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// import controller._;

/**
 * The automatic character creation view for Fatal Fantasy: Tactics Game.
 */
public class CharacterAutoCreationView extends JFrame {
    private int playerID;

    // Button labels
    public static final String RANDOMIZE = "Randomize";
    public static final String CREATE = "Create";
    public static final String RETURN = "Return";

    // UI components
    private RoundedTextField charNameField;
    private JTextArea charDetailsArea;
    private JButton btnRandomize;
    private JButton btnCreate;
    private JButton btnReturn;

    /**
     * Constructs the Automatic Character Creation UI of Fatal Fantasy: Tactics Game.
     */
    public CharacterAutoCreationView(int playerID) {
        super("Fatal Fantasy: Tactics | Player " + playerID + " Automatic Character Creation");

        this.playerID = playerID;

        initUI();
        
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    CharacterAutoCreationView.this,
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
            private Image bg = new ImageIcon("view/assets/CharCreationBG.jpg").getImage();
            
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

        centerPanel.add(Box.createVerticalStrut(40));
        String headlineImagePath = String.format("view/assets/Player%dAutoCharCreationLogo.png", playerID);
        ImageIcon logoIcon = new ImageIcon(headlineImagePath);
        Image logoImg = logoIcon.getImage().getScaledInstance(550, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);
        
        centerPanel.add(Box.createVerticalStrut(10));

        // Character name input field
        charNameField = new RoundedTextField("Enter character name", 20);
        charNameField.setMaximumSize(new Dimension(300, 35));
        centerPanel.add(charNameField);
        centerPanel.add(Box.createVerticalStrut(20));

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

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
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 50));
        buttonPanel.setOpaque(false);

        btnRandomize = new RoundedButton(RANDOMIZE);
        btnCreate = new RoundedButton(CREATE);
        btnReturn = new RoundedButton(RETURN);

        buttonPanel.add(btnRandomize);
        buttonPanel.add(btnCreate);
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
        btnRandomize.addActionListener(listener);
        btnCreate.addActionListener(listener);
        btnReturn.addActionListener(listener);
    }


    /**
     * Confirms character creation with the user.
     * 
     * @param characterName The name of the character to be created
     * @return true if the user confirms creation, false otherwise
     */
    public boolean confirmCharacterCreation(String characterName) {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to create \"" + characterName + "\"?",
            "Confirm Creation",
            JOptionPane.YES_NO_OPTION
        );

        return option == JOptionPane.YES_OPTION;
    }


    /**
     * Sets the generated character details in the text area.
     * 
     * @param details The generated character attributes
     */
    public void setGeneratedCharacterDetails(String details) {
        charDetailsArea.setText(details);
    }


    /**
     * Clears the character name field.
     */
    public void resetFields() {
        charNameField.setText("");
    }


    public String getCharacterName() {
        return charNameField.getText().trim();
    }


    public String getGeneratedCharacterDetails() {
        return charDetailsArea.getText().trim();
    }

}