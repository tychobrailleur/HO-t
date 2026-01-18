package core.db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Thorsten Dietz
 */
public class DBInfo {

    private final DatabaseMetaData databaseMetaData;

    public DBInfo(DatabaseMetaData databaseMetaData) {
        this.databaseMetaData = databaseMetaData;
    }

    /**
     * return String for java.sql.Types
     * 
     * @param type
     * @return
     */
    public String getTypeName(int type) {
        // in future we have to change some type for some db
        return switch (type) {
            case Types.BOOLEAN -> "BOOLEAN";
            case Types.BIT -> "BIT";
            case Types.INTEGER -> "INTEGER";
            case Types.CHAR -> "CHAR";
            case Types.DATE -> "DATE";
            case Types.DECIMAL -> "DECIMAL";
            case Types.DOUBLE -> "DOUBLE";
            case Types.FLOAT -> "FLOAT";
            case Types.LONGVARCHAR -> "LONGVARCHAR";
            case Types.REAL -> "REAL";
            case Types.SMALLINT -> "SMALLINT";
            case Types.TIME -> "TIME";
            case Types.TIMESTAMP -> "TIMESTAMP";
            case Types.TINYINT -> "TINYINT";
            case Types.VARCHAR -> "VARCHAR";
            default -> "";
        };
    }

    /**
     * return all TableNames from current Database
     * 
     * @return String[]
     */
    public String[] getAllTablesNames() {
        String[] types = { "TABLES", "TABLE" };
        List<String> tables = new ArrayList<>();
        ResultSet rs = null;
        try {
            if (databaseMetaData != null) {
                rs = databaseMetaData.getTables(null, null, "%", types);
                if (rs != null) {
                    while (rs.next()) {
                        tables.add(rs.getString("TABLE_NAME"));
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("database connection: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
        return tables.toArray(new String[0]);
    }
}
