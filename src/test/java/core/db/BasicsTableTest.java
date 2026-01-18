package core.db;

import core.model.misc.Basics;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BasicsTableTest {
    private Connection conn;
    private BasicsTable basicsTable;

    @BeforeAll
    void setUp() throws Exception {
        conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
        ConnectionManager connectionManager = new ConnectionManager();
        connectionManager.connect(conn);

        basicsTable = new BasicsTable(connectionManager);
        basicsTable.createTable();
    }

    @Test
    void testCreateIndexStatementCreatesCorrectStatement() {
        String[] indices = basicsTable.getCreateIndexStatement();
        Assertions.assertNotNull(indices);
        Assertions.assertEquals(1, indices.length);
        Assertions.assertEquals("CREATE INDEX IBASICS_2 ON BASICS(Datum)", indices[0]);
    }

    @Test
    void testSaveBasicsStoresRecord() throws Exception {
        Basics basics = new Basics();
        basicsTable.saveBasics(42, basics);

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM BASICS")) {
            if (rs.next()) {
                int id = rs.getInt("HRF_ID");
                Assertions.assertEquals(42, id);
            } else {
                Assertions.fail("Record not stored");
            }
        }
    }

    @AfterAll
    static void cleanUp() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
                Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE BASICS");
        }
    }
}
