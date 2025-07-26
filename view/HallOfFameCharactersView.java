package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// import controller._;

/**
 * The top characters view for Fatal Fantasy: Tactics Game.
 */
public class HallOfFameCharactersView extends JPanel {
    // Button labels
    public static final String RETURN = "Return";

    // UI components
    private JButton btnReturn;
    private JTextArea topCharactersListArea;


    /**
     * Constructs the Top Characters Viewing UI of Fatal Fantasy: Tactics Game.
     */
    public HallOfFameCharactersView() {

        initUI();
        


    }


    /**
     * Initializes the UI components and arranges them in the main layout.
     */
    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            private Image bg = new ImageIcon("view/assets/HallOfFameBG.jpg").getImage();

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

        ImageIcon logoIcon = new ImageIcon("view/assets/TopCharactersLogo.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(680, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);

        centerPanel.add(Box.createVerticalStrut(10));

        // Rounded display box for character list
        RoundedDisplayBox detailsPanel = new RoundedDisplayBox();
        detailsPanel.setPreferredSize(new Dimension(400, 500));
        detailsPanel.setMaximumSize(new Dimension(400, 500));
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Text area for character list
        topCharactersListArea = new JTextArea();
        topCharactersListArea.setFont(new Font("Serif", Font.PLAIN, 18));
        topCharactersListArea.setForeground(Color.WHITE);
        topCharactersListArea.setOpaque(false);
        topCharactersListArea.setEditable(false);
        topCharactersListArea.setLineWrap(true);
        topCharactersListArea.setWrapStyleWord(true);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(topCharactersListArea);
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

        setLayout(new BorderLayout());
        add(backgroundPanel);
    }


    /**
     * Sets the action listener for the button click events.
     * 
     * @param listener The listener
     */
    public void setActionListener(ActionListener listener) {
        btnReturn.addActionListener(listener);
    }


    /**
     * Updates the top character list display area.
     * 
     * @param text The formatted top character list string
     */
    public void updateTopCharactersList(String text) {
        topCharactersListArea.setText(text);
    }

}