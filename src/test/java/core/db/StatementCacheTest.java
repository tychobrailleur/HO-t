package core.db;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StatementCacheTest {
    private Connection conn;
    private StatementCache statementCache;

    @BeforeAll
    void setUp() throws Exception {
        conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
        ConnectionManager connectionManager = new ConnectionManager();
        connectionManager.connect(conn);
        conn.createStatement().execute("CREATE TABLE TEST (ID INT PRIMARY KEY, CONTENT VARCHAR(255))");

        statementCache = new StatementCache(connectionManager);
    }

    @Test
    void testGetPreparedStatementRetrievesStatementFromCacheWhenEnabled() throws Exception {
        statementCache.setCachedEnabled(true);
        PreparedStatement stmt = statementCache.getPreparedStatement("INSERT INTO TEST (ID, CONTENT) VALUES (?, ?)");
        Map<String, StatementCache.CachedStatementStats> stats = statementCache.getStatementStats();
        Assertions.assertEquals(1, stats.size());

        PreparedStatement otherStmt = statementCache
                .getPreparedStatement("INSERT INTO TEST (ID, CONTENT) VALUES (?, ?)");
        Assertions.assertEquals(stmt, otherStmt);
        Assertions.assertEquals(1, stats.size());
    }

    @Test
    void testGetPreparedStatementCreatesNewStatementWhenCacheNotEnabled() throws Exception {
        statementCache.setCachedEnabled(false);
        PreparedStatement stmt = statementCache.getPreparedStatement("INSERT INTO TEST (ID, CONTENT) VALUES (?, ?)");
        Map<String, StatementCache.CachedStatementStats> stats = statementCache.getStatementStats();
        Assertions.assertEquals(0, stats.size());

        PreparedStatement otherStmt = statementCache
                .getPreparedStatement("INSERT INTO TEST (ID, CONTENT) VALUES (?, ?)");
        Assertions.assertNotEquals(stmt, otherStmt);
        Assertions.assertEquals(0, stats.size());
    }

    @Test
    void testCacheGetsClearedWhenDisablingIt() throws Exception {
        statementCache.setCachedEnabled(true);
        statementCache.getPreparedStatement("INSERT INTO TEST (ID, CONTENT) VALUES (?, ?)");
        Map<String, StatementCache.CachedStatementStats> stats = statementCache.getStatementStats();
        Assertions.assertEquals(1, stats.size());

        statementCache.setCachedEnabled(false);
        Assertions.assertEquals(0, stats.size());
    }

    @Test
    void testStatsTrackDetailsAboutStatements() throws Exception {
        String stmtSql = "INSERT INTO TEST (ID, CONTENT) VALUES (?, ?)";
        statementCache.setCachedEnabled(true);
        statementCache.getPreparedStatement(stmtSql);
        Map<String, StatementCache.CachedStatementStats> stats = statementCache.getStatementStats();
        Assertions.assertEquals(1, stats.size());

        StatementCache.CachedStatementStats rec = stats.get(stmtSql);
        Assertions.assertNotNull(rec);
        Assertions.assertTrue(Duration.between(rec.created(), Instant.now()).getSeconds() < 1);
        Assertions.assertTrue(Duration.between(rec.lastAccessed(), Instant.now()).getSeconds() < 1);
    }

    @AfterAll
    static void cleanUp() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "")) {
            conn.createStatement().execute("DROP TABLE TEST");
        }
    }
}
