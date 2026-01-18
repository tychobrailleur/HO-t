package core.jmx;

import core.db.DBManager;
import core.db.StatementCache;
import java.util.Map;
import java.util.stream.Collectors;

public class StatementCacheMonitor implements StatementCacheMonitorMBean {

    @Override
    public Map<String, String> getStatistics() {
        var connectionManager = DBManager.instance().getConnectionManager();
        return connectionManager.getStatementCache().getStatementStats().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().toString()));
    }

    @Override
    public int getCachedStatementCount() {
        var connectionManager = DBManager.instance().getConnectionManager();
        return connectionManager.getStatementCache().getStatementStats().size();
    }

    @Override
    public void setCacheEnabled(boolean enabled) {
        var connectionManager = DBManager.instance().getConnectionManager();
        connectionManager.getStatementCache().setCachedEnabled(enabled);
    }
}
