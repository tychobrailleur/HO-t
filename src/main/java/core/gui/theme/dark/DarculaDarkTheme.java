package core.gui.theme.dark;

import core.model.UserParameter;

import java.util.HashMap;
import java.util.Map;

public class DarculaDarkTheme extends DarkTheme {

    public final static String THEME_NAME = "Darcula";

    /**
     * @inheritDoc
     */
    @Override
    public String getName() {
        return THEME_NAME;
    }

    @Override
    public boolean loadTheme() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("fontSize", UserParameter.instance().schriftGroesse);

        return super.enableTheme(null, properties);
    }
}
