package view;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
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

// import controller._;

/**
 * The battle modes view for Fatal Fantasy: Tactics Game.
 */
public class BattleModesView extends JFrame {
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
        super("Fatal Fantasy: Tactics | Battle Mode Selection");

        initUI();
        
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    BattleModesView.this,
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

        setContentPane(backgroundPanel);
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