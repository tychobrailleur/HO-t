package module.training.ui.renderer;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import module.training.ui.comp.BestPositionCell;
import module.training.ui.comp.PlayerNameCell;
import module.training.ui.comp.VerticalIndicator;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class OutputTableRenderer extends DefaultTableCellRenderer {
    //~ Methods ------------------------------------------------------------------------------------

    private static final long serialVersionUID = 7179773036740605371L;

    private static final Color TABLE_BG = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
    private static final Color SELECTION_BG = ThemeManager.getColor(HOColorName.TABLE_SELECTION_BG);
    private static final Color TABLE_FG = ThemeManager.getColor(HOColorName.TABLEENTRY_FG);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);

        Color bg_color;

        if (column < 3 && isSelected) {
            if (column == 0) {
                PlayerNameCell pnc = (PlayerNameCell) value;
                pnc.setForeground(this.getForeground());
                pnc.setBackground(this.getBackground());
                return pnc;
            } else {
                return this;
            }
        }

        // Reset default values
        this.setForeground(TABLE_FG);
        if (isSelected)
            this.setBackground(SELECTION_BG);
        else
            this.setBackground(TABLE_BG);

        if ((column > 2) && (column < 11)) {
            VerticalIndicator vi = (VerticalIndicator) value;

            // Set background and make it visible.
            vi.setBackground(TABLE_BG.brighter());
            vi.setOpaque(true);

            return vi;
        }

        if (column < 3 && !isSelected) {
            int speed = (int) table.getValueAt(row, 12);

            // Speed range is 16 to 125
            if (speed > (125 + 50) / 2) {
                bg_color = ThemeManager.getColor(HOColorName.PLAYER_SKILL_SPECIAL_BG);
            } else if (speed > (50 + 16) / 2) {
                bg_color = ThemeManager.getColor(HOColorName.PLAYER_SKILL_BG);
            } else {
                bg_color = ThemeManager.getColor(HOColorName.TABLEENTRY_BG);
            }

            if (column == 0) {
                PlayerNameCell pnc = (PlayerNameCell) value;
                // Reset default values
                pnc.setForeground(ThemeManager.getColor(HOColorName.TABLEENTRY_FG));
                pnc.setBackground(bg_color);
                return pnc;
            } else {
                setBackground(bg_color);
            }

        }

        return cell;
    }
}
