package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;

import javax.swing.JLabel;

/**
 * A JLabel with an outlined text effect.
 * <p>
 * This reusable Swing component is designed for emphasizing headers or titles
 * in the GUI, such as character names or battle titles, by rendering a white
 * filled label text with a black outline.
 * <p>
 * This view component is strictly visual and adheres to MVC design.
 *
 * <p><strong>Usage:</strong></p>
 * <pre>
 *     OutlinedLabel label = new OutlinedLabel("Victory!");
 *     label.setFont(new Font("Serif", Font.BOLD, 24));
 *     somePanel.add(label);
 * </pre>
 */
public class OutlinedLabel extends JLabel {

    /**
     * Constructs a label with outlined text.
     *
     * @param text The text to display
     */
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

        GlyphVector gv = font.createGlyphVector(g2.getFontRenderContext(), text);
        Shape textShape = gv.getOutline();
        Rectangle bounds = textShape.getBounds();

        double x = (getWidth() - bounds.width) / 2.0 - bounds.x;
        double y = (getHeight() - bounds.height) / 2.0 - bounds.y;

        g2.translate(x, y);

        g2.setColor(Color.BLACK); // Outline color
        g2.setStroke(new BasicStroke(2f));
        g2.draw(textShape);

        g2.setColor(getForeground()); // Fill color
        g2.fill(textShape);

        g2.dispose();
    }
}
