package core.gui.theme;

import javax.swing.*;
import java.awt.*;

public class ColourIcon implements Icon {

    private final static int ICON_SIZE = 8;
    private final Color colour;

    public ColourIcon(Color colour) {
        this.colour = colour;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(Color.BLACK);
        g.drawRect(x, y, ICON_SIZE, ICON_SIZE);

        g.setColor(colour);
        g.fillRect( x+1, y+1, ICON_SIZE-1, ICON_SIZE-1);
    }

    @Override
    public int getIconWidth() {
        return ICON_SIZE;
    }

    @Override
    public int getIconHeight() {
        return ICON_SIZE;
    }
}
