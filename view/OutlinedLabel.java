package view;

import javax.swing.*;
import java.awt.*;

/**
 * JLabel that draws a simple black outline around the text to improve
 * readability on busy backgrounds.
 */
public class OutlinedLabel extends JLabel {

    public OutlinedLabel(String text) {
        super(text);
        setOpaque(false);
        setForeground(Color.WHITE);
        setFont(getFont().deriveFont(Font.BOLD, 18f));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String txt = getText();
        FontMetrics fm = g2.getFontMetrics();
        int x;
        switch (getHorizontalAlignment()) {
            case CENTER -> x = (getWidth() - fm.stringWidth(txt)) / 2;
            case RIGHT -> x = getWidth() - fm.stringWidth(txt) - getInsets().right;
            default -> x = getInsets().left;
        }
        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

        g2.setColor(Color.BLACK);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                g2.drawString(txt, x + dx, y + dy);
            }
        }
        g2.setColor(getForeground());
        g2.drawString(txt, x, y);
        g2.dispose();
    }
}
