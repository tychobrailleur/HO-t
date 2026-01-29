package core.context;

import core.db.DBManager;
import core.model.HOModelManager;

/**
 * ApplicationContext acts as the registry for application-wide dependencies.
 * It replaces the usage of static Singletons by providing a centralized
 * point for dependency retrieval and wiring.
 */
public class ApplicationContext {

    private final DBManager dbManager;
    private final HOModelManager modelManager;
    private final core.gui.RefreshManager refreshManager;
    private final core.gui.theme.ThemeManager themeManager;
    private final EventBus eventBus;

    public ApplicationContext(DBManager dbManager, HOModelManager modelManager,
            core.gui.RefreshManager refreshManager, core.gui.theme.ThemeManager themeManager) {
        this.dbManager = dbManager;
        this.modelManager = modelManager;
        this.refreshManager = refreshManager;
        this.themeManager = themeManager;
        this.eventBus = new EventBus();
    }

    public DBManager getDBManager() {
        return dbManager;
    }

    public HOModelManager getModelManager() {
        return modelManager;
    }

    public core.gui.RefreshManager getRefreshManager() {
        return refreshManager;
    }

    public core.gui.theme.ThemeManager getThemeManager() {
        return themeManager;
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
