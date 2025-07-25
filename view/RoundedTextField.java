package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class RoundedTextField extends JTextField {
    private String placeholder;

    public RoundedTextField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;
        setOpaque(false);
        setFont(new Font("Serif", Font.PLAIN, 22));
        setForeground(new Color(80, 50, 30));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        super.paintComponent(g);
        g2.dispose();

        // Draw placeholder text
        if (getText().isEmpty() && !isFocusOwner()) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setFont(getFont());
            g2d.setColor(Color.GRAY);
            Insets insets = getInsets();
            g2d.drawString(placeholder, insets.left + 5, getHeight() / 2 + getFont().getSize() / 2 - 2);
            g2d.dispose();
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
        g2.dispose();
    }
}