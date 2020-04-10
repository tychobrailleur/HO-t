package core.gui.theme.dark;

import com.github.weisj.darklaf.DarkLaf;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.SolarizedDarkTheme;
import core.gui.theme.HOBooleanName;
import core.gui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class DarkTheme {

    public boolean enableTheme(Component mainWindow, Map<String, Object> properties) {

        try {
            LafManager.setTheme(new SolarizedDarkTheme());
            UIManager.setLookAndFeel(DarkLaf.class.getCanonicalName());
            SwingUtilities.updateComponentTreeUI(mainWindow);

            ThemeManager.instance().put(HOBooleanName.IMAGEPANEL_BG_PAINTED, false);

        } catch (Exception e) {
            // log error
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
