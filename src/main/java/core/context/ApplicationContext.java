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

    public ApplicationContext(DBManager dbManager, HOModelManager modelManager) {
        this.dbManager = dbManager;
        this.modelManager = modelManager;
    }

    public DBManager getDBManager() {
        return dbManager;
    }

    public HOModelManager getModelManager() {
        return modelManager;
    }
}
