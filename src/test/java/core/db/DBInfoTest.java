package core.db;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

class DBInfoTest {
    @Test
    void testGetTypeNameReturnsCorrectString() {
        DBInfo dbInfo = new DBInfo(null);
        Assertions.assertEquals("BOOLEAN", dbInfo.getTypeName(Types.BOOLEAN));
        Assertions.assertEquals("BIT", dbInfo.getTypeName(Types.BIT));
        Assertions.assertEquals("INTEGER", dbInfo.getTypeName(Types.INTEGER));
        Assertions.assertEquals("CHAR", dbInfo.getTypeName(Types.CHAR));
        Assertions.assertEquals("DATE", dbInfo.getTypeName(Types.DATE));
        Assertions.assertEquals("DECIMAL", dbInfo.getTypeName(Types.DECIMAL));
        Assertions.assertEquals("DOUBLE", dbInfo.getTypeName(Types.DOUBLE));
        Assertions.assertEquals("FLOAT", dbInfo.getTypeName(Types.FLOAT));
        Assertions.assertEquals("LONGVARCHAR", dbInfo.getTypeName(Types.LONGVARCHAR));
        Assertions.assertEquals("REAL", dbInfo.getTypeName(Types.REAL));
        Assertions.assertEquals("SMALLINT", dbInfo.getTypeName(Types.SMALLINT));
        Assertions.assertEquals("TIME", dbInfo.getTypeName(Types.TIME));
        Assertions.assertEquals("TIMESTAMP", dbInfo.getTypeName(Types.TIMESTAMP));
        Assertions.assertEquals("TINYINT", dbInfo.getTypeName(Types.TINYINT));
        Assertions.assertEquals("VARCHAR", dbInfo.getTypeName(Types.VARCHAR));
        Assertions.assertEquals("", dbInfo.getTypeName(42));
    }

    @Test
    void testGetAllTableNames() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "")) {
            conn.createStatement().execute("CREATE TABLE TEST (ID INTEGER PRIMARY KEY)");

            DBInfo dbInfo = new DBInfo(conn.getMetaData());
            List<String> tableNames = dbInfo.getAllTablesNames();
            Assertions.assertEquals(1, tableNames.size());
            Assertions.assertEquals("TEST", tableNames.get(0));
        }
    }

    @AfterAll
    static void cleanUp() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
                Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE TEST");
        }
    }
}
