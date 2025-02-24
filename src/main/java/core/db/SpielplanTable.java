package core.db;

import core.util.HODateTime;
import core.util.HOLogger;
import module.series.MatchFixtures;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.util.Vector;

final class SpielplanTable extends AbstractTable {
	static final String TABLENAME = "SPIELPLAN";
	
	SpielplanTable(ConnectionManager adapter){
		super(TABLENAME,adapter);
		idColumns = 2;
	}

	@Override
	protected void initColumns() {
		columns = new ColumnDescriptor[]{
				ColumnDescriptor.Builder.newInstance().setColumnName("LigaID").setGetter((p) -> ((MatchFixtures) p).getLigaId()).setSetter((p, v) -> ((MatchFixtures) p).setLigaId((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("Saison").setGetter((p) -> ((MatchFixtures) p).getSaison()).setSetter((p, v) -> ((MatchFixtures) p).setSaison((int) v)).setType(Types.INTEGER).isNullable(false).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("LigaName").setGetter((p) -> ((MatchFixtures) p).getLigaName()).setSetter((p, v) -> ((MatchFixtures) p).setLigaName((String) v)).setType(Types.VARCHAR).setLength(256).isNullable(true).build(),
				ColumnDescriptor.Builder.newInstance().setColumnName("FetchDate").setGetter((p) -> ((MatchFixtures) p).getFetchDate().toDbTimestamp()).setSetter((p, v) -> ((MatchFixtures) p).setFetchDate((HODateTime) v)).setType(Types.TIMESTAMP).isNullable(false).build()
		};
	}

	private final String getAllSpielplaeneSql = createSelectStatement("ORDER BY Saison DESC");

	/**
	 * Returns all the game schedules from the database.
	 */
	List<MatchFixtures> getAllSpielplaene() {
		return load(MatchFixtures.class, connectionManager.executePreparedQuery(getAllSpielplaeneSql));
	}

	/**
	 * Gets a game schedule from the database; returns the latest if either param is -1.
	 *
	 * @param ligaId ID of the series.
	 * @param saison Season number.
	 */
	MatchFixtures getSpielplan(int ligaId, int saison) {
		return loadOne(MatchFixtures.class, ligaId, saison);
	}

	private final String getLigaID4SaisonIDSql = "SELECT LigaID FROM "+getTableName()+" WHERE Saison=? ORDER BY FETCHDATE DESC LIMIT 1";

	/**
	 * Gibt eine Ligaid zu einer Seasonid zurück, oder -1, wenn kein Eintrag in der DB gefunden
	 * wurde
	 */
	int getLigaID4SaisonID(int seasonid) {
		int ligaid = -1;

		try (final ResultSet rs = connectionManager.executePreparedQuery(getLigaID4SaisonIDSql, seasonid)) {
			assert rs != null;
			if (rs.next()) {
				ligaid = rs.getInt("LigaID");
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getLigaID4SeasonID : " + e);
		}
		return ligaid;
	}
	
	/**
	 * Saves a game schedule ({@link MatchFixtures}) with its associated fixtures.
	 *
	 * @param plan Spielplan to save.
	 */
	void storeSpielplan(MatchFixtures plan) {
		if (plan != null) {
			plan.setIsStored(isStored(plan.getLigaId(), plan.getSaison()));
			store(plan);
		}
	}

	private final String loadLatestSpielplanSql = createSelectStatement(" ORDER BY FetchDate DESC LIMIT 1");

	public MatchFixtures getLatestSpielplan() {
		return loadOne(MatchFixtures.class, this.connectionManager.executePreparedQuery(loadLatestSpielplanSql));
	}

	/**
	 * load all league ids
	 */
	Integer[] getAllLigaIDs() {
		final Vector<Integer> vligaids = new Vector<>();
		Integer[] ligaids = null;
		final String sql = "SELECT DISTINCT LigaID FROM SPIELPLAN";

		try (final ResultSet rs = connectionManager.executeQuery(sql)) {
			while (rs != null && rs.next()) {
				vligaids.add(rs.getInt("LigaID"));
			}

			ligaids = new Integer[vligaids.size()];
			for (int i = 0; i < vligaids.size(); i++) {
				ligaids[i] = vligaids.get(i);
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),"DatenbankZugriff.getAllLigaIDs : " + e);
		}

		return ligaids;
	}

}
