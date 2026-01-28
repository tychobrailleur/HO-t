package core.db;

import core.util.HOLogger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache for {@link PreparedStatement}s instances.
 * <p>
 * This cache tracks statistics about the various prepared statements:
 * <ul>
 * <li>Creation timestamp,</li>
 * <li>Last access timestamp,</li>
 * <li>Number of accesses.</li>
 * </ul>
 * <p>
 * The cache can be disabled by setting `cachedEnabled` to `false`. When the
 * cache
 * is disabled, the existing entries are closed and evicted, the stats dumped
 * and cleared. The cache can
 * be enabled or disabled via JMX in development mode. By default, the cache is
 * on.
 */
public class StatementCache {

    private final ConnectionManager connectionManager;
    private boolean cachedEnabled = true;
    private final Map<String, PreparedStatement> cache = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, CachedStatementStats> statementStats = Collections.synchronizedMap(new HashMap<>());

    public StatementCache(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void setCachedEnabled(boolean enabled) {
        this.cachedEnabled = enabled;
        HOLogger.instance().info(StatementCache.class, "Cache enabled = " + enabled);
        if (!enabled) {
            clearCache();
        }
    }

    public boolean isCachedEnabled() {
        return cachedEnabled;
    }

    public Map<String, CachedStatementStats> getStatementStats() {
        return statementStats;
    }

    private PreparedStatement getFromCache(String query) {
        if (cachedEnabled) {
            PreparedStatement statement = cache.get(query);
            if (statement != null) {
                CachedStatementStats stats = statementStats.get(query);
                if (stats != null) {
                    statementStats.put(query,
                            new CachedStatementStats(stats.created(), Instant.now(), stats.count() + 1));
                }
                return statement;
            }
        }
        return null;
    }

    private PreparedStatement createStatement(String query) throws SQLException {
        try {
            PreparedStatement statement = null;
            if (connectionManager.getConnection() != null) {
                statement = connectionManager.getConnection().prepareStatement(query);
            }

            if (cachedEnabled && statement != null) {
                cache.put(query, statement);
                statementStats.put(query, new CachedStatementStats(Instant.now(), Instant.now(), 1));
            }

            return statement;
        } catch (SQLException e) {
            HOLogger.instance().error(StatementCache.class,
                    "Error creating statement: " + query + "\n Error: " + e.getMessage());
            throw e;
        }
    }

    public PreparedStatement getPreparedStatement(String query) throws SQLException {
        PreparedStatement statement = getFromCache(query);
        if (statement == null) {
            statement = createStatement(query);
        }
        return statement;
    }

    private void clearCache() {
        for (Map.Entry<String, PreparedStatement> entry : cache.entrySet()) {
            try {
                if (entry.getValue() != null) {
                    entry.getValue().close();
                }
            } catch (SQLException e) {
                HOLogger.instance().error(StatementCache.class,
                        "Error closing prepared statement: " + entry.getKey() + "\n " + e.getMessage());
            }
        }
        cache.clear();
        dumpStats();
        statementStats.clear();
    }

    public void dumpStats() {
        for (Map.Entry<String, CachedStatementStats> entry : statementStats.entrySet()) {
            HOLogger.instance().info(StatementCache.class, entry.getKey() + ": " + entry.getValue());
        }
    }

}
