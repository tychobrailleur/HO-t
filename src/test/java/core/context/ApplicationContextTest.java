package core.context;

import core.db.DBManager;
import core.gui.RefreshManager;
import core.gui.theme.ThemeManager;
import core.model.HOModelManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApplicationContextTest {

    @Mock
    private DBManager dbManager;
    @Mock
    private HOModelManager modelManager;
    @Mock
    private RefreshManager refreshManager;
    @Mock
    private ThemeManager themeManager;

    @Test
    void testContextInitialization() {
        ApplicationContext context = new ApplicationContext(dbManager, modelManager, refreshManager, themeManager);

        // Verify dependencies are correctly stored
        assertSame(dbManager, context.getDBManager());
        assertSame(modelManager, context.getModelManager());
        assertSame(refreshManager, context.getRefreshManager());
        assertSame(themeManager, context.getThemeManager());

        // Verify EventBus is initialized automatically
        assertNotNull(context.getEventBus(), "EventBus should be initialized in constructor");
        assertTrue(context.getEventBus() instanceof EventBus);
    }
}
