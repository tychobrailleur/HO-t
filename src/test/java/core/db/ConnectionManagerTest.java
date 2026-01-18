package core.db;

import org.hsqldb.types.Types;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConnectionManagerTest {
    private ConnectionManager connectionManager;

    @BeforeEach
    void setUp() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
        connectionManager = new ConnectionManager();
        connectionManager.connect(conn);
        conn.createStatement().execute("CREATE TABLE TEST (ID INT PRIMARY KEY, CONTENT VARCHAR(255))");
    }

    @Test
    void testExecuteQueryThrowsSQLExceptionIfConnectionClosed() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
        connectionManager.connect(conn);
        conn.close();
        Assertions.assertThrows(SQLException.class, () -> connectionManager.executeQuery("SELECT * FROM NOTHING"));
    }

    @Test
    void testExecuteQueryThrowsIfSQLException() {
        Assertions.assertThrows(SQLException.class, () -> connectionManager.executeQuery("SELECT * FROM NOTHING"));
    }

    @Test
    void testExecuteQueryReturnsSelectResults() throws SQLException {
        Connection conn = connectionManager.getConnection();
        conn.createStatement().executeUpdate("INSERT INTO TEST (ID, CONTENT) VALUES (1, 'Hello HO!')");

        try (ResultSet rs = connectionManager.executeQuery("SELECT * FROM TEST")) {
            Assertions.assertTrue(rs.next());
            Assertions.assertEquals("Hello HO!", rs.getString(2));
        }
    }

    @Test
    void testExecuteUpdateThrowsExceptionIfConnectionClosed() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
        connectionManager.connect(conn);
        conn.close();
        Assertions.assertThrows(SQLException.class,
                () -> connectionManager.executeUpdate("INSERT INTO NOTHING (ID, NO_NO) VALUES (NULL, NULL)"));
    }

    @Test
    void testExecuteUpdateReturnsNumbersInserted() throws SQLException {
        Assertions.assertEquals(1,
                connectionManager.executeUpdate("INSERT INTO TEST (ID, CONTENT) VALUES (1, 'Hello HO!')"));
        Assertions.assertEquals(1,
                connectionManager.executeUpdate("INSERT INTO TEST (ID, CONTENT) VALUES (2, 'Hello HO!')"));
        Assertions.assertEquals(1, connectionManager.executeUpdate("UPDATE TEST SET CONTENT = 'Hej HO!' WHERE ID = 1"));
        Assertions.assertEquals(2, connectionManager.executeUpdate("UPDATE TEST SET CONTENT = 'Hallo HO!'"));
    }

    @Test
    void testGetDbInfoReturnsMetadata() {
        DBInfo dbInfo = connectionManager.getDbInfo();
        Assertions.assertEquals("TINYINT", dbInfo.getTypeName(Types.TINYINT));
    }

    @Test
    void testGetAllTableNamesReturnsAllTables() {
        List<String> tableNames = connectionManager.getAllTableNames();
        Assertions.assertEquals(1, tableNames.size());
        Assertions.assertEquals("TEST", tableNames.get(0));
    }

    @Test
    void testExecuteUpdateSQLExceptionReturnsZero() {
        Assertions.assertThrows(SQLException.class, () -> connectionManager.executeUpdate("INSERT INTO TEST "));
    }

    @Test
    void testDisconnectUnsetsConnection() {
        connectionManager.disconnect();
        Assertions.assertNull(connectionManager.getConnection());
    }

    @Test
    void testExecutePreparedQueryReturnRecords() throws SQLException {
        Connection conn = connectionManager.getConnection();
        conn.createStatement().executeUpdate("INSERT INTO TEST (ID, CONTENT) VALUES (1, 'Hello HO!')");

        try (ResultSet rs = connectionManager
                .executePreparedQuery("SELECT ID, CONTENT  FROM TEST WHERE ID = ? AND CONTENT LIKE ?", 1, "Hello%")) {
            Assertions.assertNotNull(rs);
            Assertions.assertTrue(rs.next());
            Assertions.assertEquals("Hello HO!", rs.getString(2));
        }
    }

    @Test
    void testExecutePreparedUpdateReturnNumOfRecords() throws SQLException {
        Connection conn = connectionManager.getConnection();
        conn.createStatement().executeUpdate("INSERT INTO TEST (ID, CONTENT) VALUES (1, 'Hello HO!')");

        int num = connectionManager.executePreparedUpdate("UPDATE TEST SET CONTENT = ? WHERE ID = ?", "Hallo HO!", 1);
        Assertions.assertEquals(1, num);
    }

    @AfterEach
    void cleanUp() throws SQLException {
        if (connectionManager.getConnection() != null) {
            try (Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "")) {
                conn.createStatement().execute("DROP TABLE TEST");
            }
        }
    }
}
