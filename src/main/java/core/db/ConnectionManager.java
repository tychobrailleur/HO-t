package core.db;

import core.util.HOLogger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Provides the connection functions to the database
 */
public class ConnectionManager {

    private Connection connection;
    private Statement statement;
    private DBInfo dbInfo;
    private StatementCache statementCache = new StatementCache(this);

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Statement getStatement() {
        return statement;
    }

    public StatementCache getStatementCache() {
        return statementCache;
    }

    public DBInfo getDbInfo() {
        if (dbInfo == null) {
            try {
                if (connection != null) {
                    dbInfo = new DBInfo(connection.getMetaData());
                }
            } catch (SQLException e) {
                HOLogger.instance().error(getClass(), "Error getting metadata: " + e.getMessage());
            }
        }
        return dbInfo;
    }

    /**
     * Closes the connection
     */
    public void disconnect() {
        try {
            if (connection != null) {
                try (Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY)) {
                    stmt.execute("SHUTDOWN");
                }
                connection.close();
            }
        } catch (Exception e) {
            HOLogger.instance().error(getClass(), "ConnectionManager.disconnect : " + e);
        } finally {
            connection = null;
        }
    }

    /**
     * Execute a SQL Select statement
     *
     * @param sqlStatement
     *                     Sql query with placeholders
     *
     * @return ResultSet of the query
     */
    public ResultSet executeQuery(String sqlStatement) throws SQLException {
        checkConnectionNotClosed();
        if (statement != null) {
            return statement.executeQuery(sqlStatement);
        }
        return null;
    }

    private void checkConnectionNotClosed() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Connection closed");
        }
    }

    public ResultSet executePreparedQuery(String query, Object... params) throws SQLException {
        PreparedStatement preparedStatement = statementCache.getPreparedStatement(query);
        if (preparedStatement != null) {
            return executePreparedQuery(preparedStatement, params);
        }
        return null;
    }

    private ResultSet executePreparedQuery(PreparedStatement preparedStatement, Object... params) throws SQLException {
        checkConnectionNotClosed();
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        return preparedStatement.executeQuery();
    }

    /**
     * Executes an SQL INSERT, UPDATE or DELETE statement. In addition, SQL
     * statements that return nothing, such as SQL DDL statements, can be
     * executed.
     *
     * @param sqlStatement
     *                     INSERT, UPDATE or DELETE statement
     *
     * @return either the row count for SQL Data Manipulation Language (DML)
     *         statements or 0 for SQL statements that return nothing
     *
     */
    public int executeUpdate(String sqlStatement) throws SQLException {
        checkConnectionNotClosed();
        if (statement != null) {
            return statement.executeUpdate(sqlStatement);
        }
        return 0;
    }

    public int executePreparedUpdate(String insert, Object... params) throws SQLException {
        PreparedStatement preparedStatement = statementCache.getPreparedStatement(insert);
        if (preparedStatement != null) {
            return executePreparedUpdate(preparedStatement, params);
        }
        return 0;
    }

    private int executePreparedUpdate(PreparedStatement preparedStatement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        return preparedStatement.executeUpdate();
    }

    /**
     * Connects to the requested database
     *
     * @param url
     *                 The path to the Server
     * @param user
     *                 User
     * @param password
     *                 Password
     * @param driver
     *                 The driver to user
     *
     */
    public void connect(String url, String user, String password, String driver)
            throws ClassNotFoundException, SQLException {
        // Initialise the Database Driver Object
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, user, password);
        connect(conn);
    }

    public void connect(Connection conn) throws SQLException {
        connection = conn;
        statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statementCache = new StatementCache(this);
    }

    public String[] getAllTableNames() {
        try {
            DBInfo currentDbInfo = getDbInfo();
            return currentDbInfo != null ? currentDbInfo.getAllTablesNames() : new String[0];
        } catch (Exception e) {
            HOLogger.instance().error(getClass(), "ConnectionManager.getAllTableNames : " + e);
            throw e; // Rethrowing/handling based on original behavior
        }
    }
}
