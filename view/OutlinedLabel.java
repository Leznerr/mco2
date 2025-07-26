package view;

import java.awt.*;
import javax.swing.*;
import java.awt.font.GlyphVector;

public class OutlinedLabel extends JLabel {
    public OutlinedLabel(String text) {
        super(text);
        setFont(new Font("Serif", Font.BOLD, 18));
        setForeground(Color.WHITE); // Inner fill color
    }

    @Override
    public Dimension getPreferredSize() {
        Graphics g = getGraphics();
        if (g == null) return super.getPreferredSize();
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int width = fm.stringWidth(getText()) + 4; // Add space for outline
        int height = fm.getHeight() + 4;           // Add space for outline
        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        String text = getText();
        Font font = getFont();

        // Center text with outline included
        GlyphVector gv = font.createGlyphVector(g2.getFontRenderContext(), text);
        Shape textShape = gv.getOutline();
        Rectangle bounds = textShape.getBounds();

        double x = (getWidth() - bounds.width) / 2.0 - bounds.x;
        double y = (getHeight() - bounds.height) / 2.0 - bounds.y;

        g2.translate(x, y);

        // Draw outline
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2f));
        g2.draw(textShape);

        // Fill text
        g2.setColor(getForeground());
        g2.fill(textShape);

        g2.dispose();
    }
}
