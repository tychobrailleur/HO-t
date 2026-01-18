package core.jmx;

import java.util.Map;

public interface StatementCacheMonitorMBean {
    Map<String, String> getStatistics();

    int getCachedStatementCount();

    void setCacheEnabled(boolean enabled);
}
