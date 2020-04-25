package core.gui.theme.dark;

import com.github.weisj.darklaf.DarkLaf;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import core.gui.theme.HOBooleanName;
import core.gui.theme.HOColorName;
import core.gui.theme.Theme;
import core.gui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public abstract class DarkTheme implements Theme {

    public boolean enableTheme(Component mainWindow, Map<String, Object> properties) {

        try {
            LafManager.setTheme(new DarculaTheme());
            UIManager.setLookAndFeel(DarkLaf.class.getCanonicalName());

            ThemeManager.instance().put(HOColorName.TABLEENTRY_BG, new Color(80, 80, 80));
            ThemeManager.instance().put(HOBooleanName.IMAGEPANEL_BG_PAINTED, false);

        } catch (Exception e) {
            // log error
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
