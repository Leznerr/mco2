package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// import controller._;

/**
 * The battle modes view for Fatal Fantasy: Tactics Game.
 */
public class BattleModesView extends JPanel {
    // Button labels
    public static final String PLAYER_VS_PLAYER = "Player vs Player";
    public static final String PLAYER_VS_BOT = "Player vs Bot";
    public static final String RETURN = "Return";

    // UI components
    private JButton btnPlayerVsPlayer;
    private JButton btnPlayerVsBot;
    private JButton btnReturn;
    
    
    /**
     * Constructs the Battle Mode Selection UI of Fatal Fantasy: Tactics Game.
     */
    public BattleModesView() {

        initUI();
        


    }


    /**
     * Initializes the UI components and arranges them in the main layout.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private Image bg = new ImageIcon("view/assets/CharSelectAndBattleBG.jpg").getImage();

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
        
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));

        // Add vertical space at the top
        backgroundPanel.add(Box.createVerticalStrut(100));

        // Logo image centered and scaled
        ImageIcon logoIcon = new ImageIcon("view/assets/BattleModesLogo.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(320, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(logoLabel);

        // Add vertical space between logo and buttons
        backgroundPanel.add(Box.createVerticalStrut(60));

        // Panel for buttons, centered
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        btnPlayerVsPlayer = new RoundedButton(PLAYER_VS_PLAYER);
        btnPlayerVsBot = new RoundedButton(PLAYER_VS_BOT);
        btnReturn = new RoundedButton(RETURN);

        // Add buttons with vertical spacing (how they stack)
        buttonPanel.add(btnPlayerVsPlayer);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnPlayerVsBot);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnReturn);

        // Center the button panel horizontally
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(buttonPanel);

        // Add vertical glue to push everything to the center
        backgroundPanel.add(Box.createVerticalGlue());

        setLayout(new BorderLayout());
        add(backgroundPanel);
    }


    /**
     * Sets the action listener for the button click events.
     * 
     * @param listener The listener
     */
    public void setActionListener(ActionListener listener) {
        btnPlayerVsPlayer.addActionListener(listener);
        btnPlayerVsBot.addActionListener(listener);
        btnReturn.addActionListener(listener);
    }

}