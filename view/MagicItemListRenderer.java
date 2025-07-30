package view;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import model.item.MagicItem;

/**
 * Reusable renderer for displaying {@link MagicItem} names in JLists.
 * <p>
 * This ensures magic item names are consistently rendered in white and
 * supports tool tips describing each item.
 */
public class MagicItemListRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setOpaque(false);
        if (value instanceof MagicItem mi) {
            setText((index + 1) + ". " + mi.getName());
            setToolTipText(mi.getDescription());
        }
        setForeground(Color.WHITE);
        return this;
    }
}
