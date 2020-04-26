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

            UIDefaults defaults = UIManager.getLookAndFeelDefaults();

            ThemeManager.instance().put(HOBooleanName.IMAGEPANEL_BG_PAINTED, false);

            final Color NEUTRAL_GREY = new Color(80, 80, 80);

            // Use defaults from LAF
            ThemeManager.instance().put(HOColorName.TABLEENTRY_FG, defaults.get("Label.foreground"));
            ThemeManager.instance().put(HOColorName.LABEL_FG, defaults.get("Label.foreground"));
            ThemeManager.instance().put(HOColorName.PANEL_BG, defaults.get("background"));
            ThemeManager.instance().put(HOColorName.TABLEENTRY_BG, NEUTRAL_GREY);

            ThemeManager.instance().put(HOColorName.TABLE_SELECTION_FG, Color.WHITE);
            ThemeManager.instance().put(HOColorName.TABLE_SELECTION_BG, new Color(65, 65, 65));

            ThemeManager.instance().put(HOColorName.PLAYER_SKILL_SPECIAL_BG, new Color(56, 76, 53));
            ThemeManager.instance().put(HOColorName.PLAYER_SKILL_BG, new Color(95, 86, 38));
            ThemeManager.instance().put(HOColorName.PLAYER_POS_BG, new Color(55, 71, 83));
            ThemeManager.instance().put(HOColorName.PLAYER_SUBPOS_BG, new Color(60, 60, 60));

            // Lineup
            ThemeManager.instance().put(HOColorName.LINEUP_POS_MIN_BG, NEUTRAL_GREY);

            // Matches
            ThemeManager.instance().put(HOColorName.MATCHTYPE_LEAGUE_BG, new Color(95, 86, 38));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_BG, new Color(60, 60, 60));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_FRIENDLY_BG, new Color(60, 63, 65));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_INTFRIENDLY_BG, new Color(60, 63, 65));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_INT_BG, new Color(50, 67, 67));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_CUP_BG, new Color(56, 76, 53));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_QUALIFIKATION_BG, new Color(83, 45, 45));

            ThemeManager.instance().put(HOColorName.MATCHTYPE_MASTERS_BG, new Color(80, 70, 43));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_NATIONAL_BG, new Color(57, 54, 62));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_TOURNAMENT_GROUP_BG, new Color(48, 54, 56));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_TOURNAMENT_FINALS_BG, new Color(61, 67, 68));
            ThemeManager.instance().put(HOColorName.MATCHTYPE_DIVISIONBATTLE_BG, new Color(66, 68, 80));

        } catch (Exception e) {
            // log error
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
