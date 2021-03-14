package core.db;

import core.db.user.User;
import core.db.user.UserManager;
import core.model.HOParameter;
import core.model.UserParameter;
import core.util.HOLogger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.swing.JOptionPane;
import core.HO;
import org.flywaydb.core.Flyway;

final class DBUpdater {
	JDBCAdapter m_clJDBCAdapter;
	DBManager dbManager;

	void setDbManager(DBManager dbManager) {
		this.dbManager = dbManager;
	}

	void updateDB(int DBVersion) {
		// Just add new version cases in the switch..case part
		// and leave the old ones active, so also users which
		// have skipped a version get their database updated.

		this.m_clJDBCAdapter = dbManager.getAdapter();

		int version = ((UserConfigurationTable) dbManager.getTable(UserConfigurationTable.TABLENAME)).getDBVersion();

		if (version != DBVersion) {
			// We upgrade database from version 300 (HO 3.0)
			if (version < 300){
				HOLogger.instance().log(getClass(), "DB version " + DBVersion + " is too old");
				try {
					JOptionPane.showMessageDialog(null,
							"DB is too old.\nPlease update first to HO! 3.0", "Error",
							JOptionPane.ERROR_MESSAGE);
				} catch (Exception e) {
					HOLogger.instance().log(getClass(), e);
				}
				System.exit(0);
			}

			try {
				switch (version) { // hint: fall through (no breaks) is intended
					case 301:
						updateDBv300();  // Bug#509 requires another update run of v300
						updateDBv301(DBVersion);
					case 302:
					case 399:
						updateDBv400(DBVersion);
					case 400:
					case 499:

				}

			} catch (Exception e) {
				HOLogger.instance().log(getClass(), e);
			}
		} else {
			HOLogger.instance().log(getClass(), "No DB update necessary.");
		}
	}

	private void updateDBv400(int dbVersion) throws SQLException {
		// Delete existing values to provide sane defaults.
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'spielerUebersichtsPanel_horizontalRightSplitPane'");
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'aufstellungsPanel_verticalSplitPane'");
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'aufstellungsPanel_horizontalRightSplitPane'");
		m_clJDBCAdapter.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY = 'aufstellungsPanel_horizontalLeftSplitPane'");

		if ( !columnExistsInTable("SeasonOffset", BasicsTable.TABLENAME)){
			m_clJDBCAdapter.executeUpdate("ALTER TABLE BASICS ADD COLUMN SeasonOffset INTEGER");
		}

		if (!columnExistsInTable("Duration", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN Duration INTEGER ");
		}
		if (!columnExistsInTable("MatchPart", MatchHighlightsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN MatchPart INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN EventVariation INTEGER ");
		}
		if (!columnExistsInTable("HomeGoal0", MatchDetailsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal0 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal1 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal2 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal3 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN HomeGoal4 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal0 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal1 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal2 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal3 INTEGER ");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN GuestGoal4 INTEGER ");
		}

		if (!columnExistsInTable("NAME", TAPlayerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN NAME VARCHAR (100) ");
		}

		// use defaults player formula from defaults.xml by resetting the value in the database
		try {
			AbstractTable faktorenTab = dbManager.getTable(FaktorenTable.TABLENAME);
			if (faktorenTab != null) {
				faktorenTab.dropTable();
				faktorenTab.createTable();
			}
		} catch (SQLException throwables) {
			HOLogger.instance().error(getClass(), "updateDBv400:  Faktoren table could not be reset");
			throwables.printStackTrace();
		}


		resetUserColumns();

		//create FuturePlayerTrainingTable
		if (!tableExists(FuturePlayerTrainingTable.TABLENAME)) {
			dbManager.getTable(FuturePlayerTrainingTable.TABLENAME).createTable();
		}

		updateDBVersion(dbVersion, 400);
	}

	private void updateDBv301(int dbVersion) throws SQLException {

		m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ALTER COLUMN isDerby SET DATA TYPE BOOLEAN");
		m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ALTER COLUMN isNeutral SET DATA TYPE BOOLEAN");

		if (!columnExistsInTable("EVENT_INDEX", MatchHighlightsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN EVENT_INDEX INTEGER");
		}

		if (!columnExistsInTable("INJURY_TYPE", MatchHighlightsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS ADD COLUMN INJURY_TYPE TINYINT");
		}

		if (columnExistsInTable("TYP", MatchHighlightsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("UPDATE MATCHHIGHLIGHTS SET MATCH_EVENT_ID = (TYP * 100) + SUBTYP WHERE MATCH_EVENT_ID IS NULL");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS DROP TYP");
		}

		if (!columnExistsInTable("LastMatchDate", SpielerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN LastMatchDate VARCHAR (100)");
		}
		if (!columnExistsInTable("LastMatchRating", SpielerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN LastMatchRating INTEGER");
		}
		if (!columnExistsInTable("LastMatchId", SpielerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN LastMatchId INTEGER");
		}

		Arrays.asList("HEIMTORE", "GASTTORE", "SUBTYP").forEach(s -> {
			try {
				if (columnExistsInTable(s, MatchHighlightsTable.TABLENAME)) {
					m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHHIGHLIGHTS DROP " + s);
				}
			} catch (SQLException e) {
				HOLogger.instance().log(getClass(), e);
			}
		});

		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchdetails_heimid_idx ON MATCHDETAILS (HEIMID)");
		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchdetails_gastid_idx ON MATCHDETAILS (GASTID)");
		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchkurzinfo_heimid_idx ON MATCHESKURZINFO (HEIMID)");
		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchkurzinfo_gastid_idx ON MATCHESKURZINFO (GASTID)");
		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchhighlights_teamid_idx ON MATCHHIGHLIGHTS (TEAMID)");
		m_clJDBCAdapter.executeUpdate("CREATE INDEX IF NOT EXISTS matchhighlights_eventid_idx ON MATCHHIGHLIGHTS (MATCH_EVENT_ID)");

		Arrays.asList("GlobalRanking", "LeagueRanking", "RegionRanking", "PowerRating").forEach(s -> {
			try {
				if (!columnExistsInTable(s, VereinTable.TABLENAME)) {
					m_clJDBCAdapter.executeUpdate(String.format("ALTER TABLE VEREIN ADD COLUMN %s INTEGER", s));
				}
			} catch (SQLException e) {
				HOLogger.instance().log(getClass(), e);
			}
		});

		Arrays.asList("TWTrainer", "Physiologen").forEach(s -> {
			try {
				if (columnExistsInTable(s, VereinTable.TABLENAME)) {
					m_clJDBCAdapter.executeUpdate("ALTER TABLE VEREIN DROP " + s);
				}
			} catch (SQLException e) {
				HOLogger.instance().log(getClass(), e);
			}
		});

		updateDBVersion(dbVersion, 301);

	}

	private void updateDBv300() throws SQLException {
		// HO 3.0

		// delete old divider locations
		m_clJDBCAdapter
				.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='teamAnalyzer_LowerLefSplitPane'");
		m_clJDBCAdapter
				.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='teamAnalyzer_UpperLeftSplitPane'");
		m_clJDBCAdapter
				.executeUpdate("DELETE FROM USERCONFIGURATION WHERE CONFIG_KEY='teamAnalyzer_MainSplitPane'");

		//store ArenaId into MATCHESKURZINFO table
		if (!columnExistsInTable("ArenaId", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN ArenaId INTEGER");
		}

		//store RegionId into MATCHESKURZINFO table
		if (!columnExistsInTable("RegionId", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN RegionId INTEGER");
		}

		//store Weather into MATCHESKURZINFO table
		if (!columnExistsInTable("Weather", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN Weather INTEGER");
		}

		//store WeatherForecast into MATCHESKURZINFO table
		if (!columnExistsInTable("WeatherForecast", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN WeatherForecast INTEGER");
		}

		//store isDerby into MATCHESKURZINFO table
		if (!columnExistsInTable("isDerby", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN isDerby BOOLEAN");
		}

		//store isNeutral into MATCHESKURZINFO table
		if (!columnExistsInTable("isNeutral", MatchesKurzInfoTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHESKURZINFO ADD COLUMN isNeutral BOOLEAN");
		}

		//store Salary into TA_PLAYER table
		if (!columnExistsInTable("SALARY", TAPlayerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN SALARY INTEGER");
		}

		//store Stamina  into TA_PLAYER table
		if (!columnExistsInTable("STAMINA", TAPlayerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN STAMINA INTEGER");
		}

		//store MotherClubBonus  into TA_PLAYER table
		if (!columnExistsInTable("MOTHERCLUBBONUS", TAPlayerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN MOTHERCLUBBONUS BOOLEAN");
		}

		//store Loyalty  into TA_PLAYER table
		if (!columnExistsInTable("LOYALTY", TAPlayerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE TA_PLAYER ADD COLUMN LOYALTY INTEGER");
		}

		//store RATINGINDIRECTSETPIECESATT  into MATCHDETAILS table
		if (!columnExistsInTable("RATINGINDIRECTSETPIECESATT", MatchDetailsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN RATINGINDIRECTSETPIECESATT INTEGER");
		}

		//store RATINGINDIRECTSETPIECESDEF  into MATCHDETAILS table
		if (!columnExistsInTable("RATINGINDIRECTSETPIECESDEF", MatchDetailsTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE MATCHDETAILS ADD COLUMN RATINGINDIRECTSETPIECESDEF INTEGER");
		}

		//store FirstName, Nickname  into Playertable
		if (!columnExistsInTable("FirstName", SpielerTable.TABLENAME)) {
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN FirstName VARCHAR (100)");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ADD COLUMN NickName VARCHAR (100)");
			m_clJDBCAdapter.executeUpdate("ALTER TABLE SPIELER ALTER COLUMN Name RENAME TO LastName");
		}

		// Delete league plans which are not of our own team
		try {
			// find own league plans
			int teamId = getTeamId();
			// select saison,ligaid from paarung where heimid=520472 group by saison,ligaid
			HashMap<Integer, Integer> ownLeaguePlans = new HashMap<>();
			ResultSet rs = m_clJDBCAdapter.executeQuery("select saison,ligaid from paarung where heimid=" + teamId + " group by saison,ligaid");
			if (rs != null) {
				while (rs.next()) {
					int saison = rs.getInt(1);
					int league = rs.getInt(2);
					ownLeaguePlans.put(saison, league);
				}
				rs.close();
			}
			// delete entries in SPIELPLAN and PAARUNG which are not from own team
			rs = m_clJDBCAdapter.executeQuery("select saison,ligaid from spielplan");
			if (rs != null) {
				while (rs.next()) {
					int saison = rs.getInt(1);
					int league = rs.getInt(2);
					if (!ownLeaguePlans.containsKey(saison) || ownLeaguePlans.get(saison) != league) {
						// league is not our own one
						m_clJDBCAdapter.executeUpdate("DELETE FROM spielplan WHERE ligaid=" + league + " and saison=" + saison);
						m_clJDBCAdapter.executeUpdate("DELETE FROM paarung WHERE ligaid=" + league + " and saison=" + saison);
					}
				}
				rs.close();
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
		HOLogger.instance().info(DBUpdater.class, "updateDBv300() successfully completed");
	}

	private void updateDBVersion(int DBVersion, int version) {
		if (version < DBVersion) {
			if(!HO.isDevelopment()) {
				HOLogger.instance().info(DBUpdater.class, "Update to " + version + " done. Updating DBVersion");
				dbManager.saveUserParameter("DBVersion", version);
			}
			else {
				HOLogger.instance().debug(DBUpdater.class, "Update to " + version + " done but this is a development version so DBVersion will remain unchanged");
			}
		}
		else if (version == DBVersion){
			if(!HO.isDevelopment()) {
				HOLogger.instance().info(DBUpdater.class, "Update complete, setting DBVersion to " + version);
				dbManager.saveUserParameter("DBVersion", version);
			}
			else {
				HOLogger.instance().debug(DBUpdater.class, "Update to " + version + " complete but this is a development version so DBVersion will remain unchanged");
			}
		}
		else {
			HOLogger.instance().error(DBUpdater.class,
					"Error trying to set DB version to unidentified value:  " + version
							+ " (isDevelopment=" + HO.isDevelopment() + ")");
		}
	}

	private int getTeamId() {
		try {
			ResultSet rs = m_clJDBCAdapter.executeQuery("select teamid from basics limit 1");
			if (rs != null) {
				rs.first();
				int ret =  rs.getInt(1);
				rs.close();
				return ret;
			}
		} catch (SQLException e) {
			HOLogger.instance().log(getClass(), e);
		}
		return 0;
	}

	private boolean columnExistsInTable(String columnName, String tableName) throws SQLException {
		String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME = '"
				+ tableName.toUpperCase()
				+ "' AND COLUMN_NAME = '"
				+ columnName.toUpperCase()
				+ "'";
		ResultSet rs = this.m_clJDBCAdapter.executeQuery(sql);
		if ( rs != null )return rs.next();
		return false;
	}

	private boolean tableExists(String tableName) throws SQLException {
		String sql = "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_NAME = '"
				+ tableName.toUpperCase() + "'";
		ResultSet rs = this.m_clJDBCAdapter.executeQuery(sql);
		if ( rs != null )return rs.next();
		return false;	}

	private void resetUserColumns() {
		HOLogger.instance().debug(getClass(), "Resetting player overview rows.");
		String sql = "DELETE FROM USERCOLUMNS WHERE COLUMN_ID BETWEEN 2000 AND 3000";
		m_clJDBCAdapter.executeQuery(sql);

		HOLogger.instance().debug(getClass(), "Resetting lineup overview rows.");
		sql = "DELETE FROM USERCOLUMNS WHERE COLUMN_ID BETWEEN 3000 AND 4000";
		m_clJDBCAdapter.executeQuery(sql);
	}

	public void initialiseDb() {
		final User currentUser = UserManager.instance().getCurrentUser();
		final Flyway flyway = Flyway.configure()
				.dataSource(
						currentUser.getDbURL(),
						currentUser.getDbUsername(),
						currentUser.getDbPwd())
				.load();
		var result = flyway.migrate();
		HOLogger.instance().info(DBManager.class, "DB init result: " + result);

		UserConfigurationTable configTable = (UserConfigurationTable) dbManager
				.getTable(UserConfigurationTable.TABLENAME);
		configTable.store(UserParameter.instance());
		configTable.store(HOParameter.instance());
	}

	public void migrateDb(int DBVersion) {
		// Check if there are any updates on the database to be done.
		// Get the DB up to the point where Flyway takes over.
		updateDB(DBVersion);

		// Baseline DB before running migrations
		final User currentUser = UserManager.instance().getCurrentUser();
		final Flyway flyway = Flyway.configure()
				.dataSource(
						currentUser.getDbURL(),
						currentUser.getDbUsername(),
						currentUser.getDbPwd())
				.load();

		try {
			if (!tableExists("flyway_schema_history")) {
				flyway.baseline();
			}
		} catch (SQLException e) {
			HOLogger.instance().error(getClass(), "Error when trying to baseline existing database: " + e);
		}
		flyway.migrate();
	}

}
