package core;

import core.db.PersistenceManager;
import core.file.hrf.HRF;
import core.model.HOModel;
import core.util.HODateTime;

public class HOModelBuilder {

    private HRF hrf = null;
    private PersistenceManager persistenceManager = null;

    public HOModelBuilder hrfId(int id) {
        hrf = new HRF(id, HODateTime.now());
        return this;
    }

    public HOModelBuilder persistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
        return this;
    }

    public HOModel build() {
        HOModel hoModel = new HOModel(hrf != null ? hrf.getHrfId() : -1);
        hoModel.setPersistenceManager(persistenceManager);
        return hoModel;
    }
}
