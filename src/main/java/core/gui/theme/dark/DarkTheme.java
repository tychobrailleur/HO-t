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

            ThemeManager.instance().put(HOBooleanName.IMAGEPANEL_BG_PAINTED, false);

            ThemeManager.instance().put(HOColorName.PANEL_BG, new Color(60, 63, 65));
            ThemeManager.instance().put(HOColorName.TABLEENTRY_BG, new Color(80, 80, 80));
            ThemeManager.instance().put(HOColorName.TABLEENTRY_FG, Color.WHITE);

            ThemeManager.instance().put(HOColorName.TABLE_SELECTION_FG, Color.WHITE);
            ThemeManager.instance().put(HOColorName.TABLE_SELECTION_BG, new Color(65, 65, 65));

            ThemeManager.instance().put(HOColorName.PLAYER_SKILL_SPECIAL_BG, new Color(56, 76, 53));
            ThemeManager.instance().put(HOColorName.PLAYER_SKILL_BG, new Color(95, 86, 38));
            ThemeManager.instance().put(HOColorName.PLAYER_POS_BG, new Color(55, 71, 83));
            ThemeManager.instance().put(HOColorName.PLAYER_SUBPOS_BG, new Color(60, 60, 60));

            ThemeManager.instance().put(HOColorName.LINEUP_POS_MIN_BG, new Color(80, 80, 80));

        } catch (Exception e) {
            // log error
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
