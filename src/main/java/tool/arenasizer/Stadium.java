package tool.arenasizer;

import core.db.AbstractTable;
import core.util.AmountOfMoney;
import core.util.HODateTime;
import core.util.HOLogger;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Properties;

public class Stadium extends AbstractTable.Storable {

	/**
	 * HRF-ID
	 */
	private int hrfId;

	/**
	 * Arena-ID
	 */
	private int arenaId;

	/**
	 * Arena-Name
	 */
	private String stadiumName = "";

	/**
	 * Terraces
	 */
	private int terraces;

	/**
	 * Basic Seating
	 */
	private int basicSeating;

	/**
	 * Seats under Roof
	 */
	private int underRoofSeating;

	/**
	 * VIP Boxes
	 */
	private int vipBox;

	/**
	 * Expansion?
	 */
	private boolean underConstruction;

	/**
	 * Expansion of Terraces
	 */
	private int terracesUnderConstruction;

	/**
	 * Expansion of Basic Seating
	 */
	private int basicSeatingUnderConstruction;

	/**
	 * Expansion of Seats under Roof
	 */
	private int underRoofSeatingUnderConstruction;

	/**
	 * Expansion of VIP Boxes
	 */
	private int vipBoxUnderConstruction;

	/**
	 * Cost of Expansion
	 */
	private AmountOfMoney expansionCosts;

	/**
	 * Rebuilt date
	 */
	private HODateTime rebuiltDate;

	/**
	 * Expansion date
	 */
	private HODateTime expansionDate;

	public Stadium() {
	}

	public Stadium(Properties properties) {
		// 'seattotal' and 'expandingSseatTotal' are currently not read
		arenaId = NumberUtils.toInt(properties.getProperty("arenaid"), 0);
		stadiumName = properties.getProperty("arenaname", "");
		terraces = NumberUtils.toInt(properties.getProperty("antalstaplats"), 0);
		basicSeating = NumberUtils.toInt(properties.getProperty("antalsitt"), 0);
		underRoofSeating = NumberUtils.toInt(properties.getProperty("antaltak"), 0);
		vipBox = NumberUtils.toInt(properties.getProperty("antalvip"), 0);
		terracesUnderConstruction = NumberUtils.toInt(properties.getProperty("expandingstaplats"), 0);
		basicSeatingUnderConstruction = NumberUtils.toInt(properties.getProperty("expandingsitt"), 0);
		underRoofSeatingUnderConstruction = NumberUtils.toInt(properties.getProperty("expandingtak"), 0);
		vipBoxUnderConstruction = NumberUtils.toInt(properties.getProperty("expandingvip"), 0);
		underConstruction = NumberUtils.toInt(properties.getProperty("isexpanding"), 0) > 0;
		if (underConstruction) {
			expansionCosts = AmountOfMoney.parse(properties.getProperty("expandcost"));
		}
		rebuiltDate = getArenaDate(properties, "rebuiltdate");
		expansionDate = getArenaDate(properties, "expansiondate");
	}

	private HODateTime getArenaDate(Properties properties, String key) {
		try {
			return HODateTime.fromHT(properties.getProperty(key));
		} catch (Exception e) {
			HOLogger.instance().warning(this.getClass(),
					e.getMessage() + " parsing arena " + key + ": " + properties.getProperty(key));
			return null;
		}
	}

	public int getTotalSize() {
		return getTerraces() + getBasicSeating() + getUnderRoofSeating() + getVipBox();
	}

	public Optional<Integer> getTotalSizeUnderConstruction() {
		return isUnderConstruction() ? Optional.of(getTerracesUnderConstruction() + getBasicSeatingUnderConstruction()
				+ getUnderRoofSeatingUnderConstruction() + getVipBoxUnderConstruction()) : Optional.empty();
	}

	public Optional<Integer> getFutureTotalSize() {
		return getTotalSizeUnderConstruction().map(expansionSize -> expansionSize + getTotalSize());
	}

	public Optional<Integer> getFutureTerraces() {
		return isUnderConstruction() ? Optional.of(getTerraces() + getTerracesUnderConstruction()) : Optional.empty();
	}

	public Optional<Integer> getFutureBasicSeating() {
		return isUnderConstruction() ? Optional.of(getBasicSeating() + getBasicSeatingUnderConstruction())
				: Optional.empty();
	}

	public Optional<Integer> getFutureUnderRoofSeating() {
		return isUnderConstruction() ? Optional.of(getUnderRoofSeating() + getUnderRoofSeatingUnderConstruction())
				: Optional.empty();
	}

	public Optional<Integer> getFutureVipBoxes() {
		return isUnderConstruction() ? Optional.of(getVipBox() + getVipBoxUnderConstruction()) : Optional.empty();
	}

	public BigDecimal getExpansionCostsInSwedishKrona() {
		if (this.expansionCosts != null)
			return this.expansionCosts.getSwedishKrona();
		return null;
	}

	public int getHrfId() {
		return hrfId;
	}

	public void setHrfId(int hrfId) {
		this.hrfId = hrfId;
	}

	public int getArenaId() {
		return arenaId;
	}

	public void setArenaId(int arenaId) {
		this.arenaId = arenaId;
	}

	public String getStadiumName() {
		return stadiumName;
	}

	public void setStadiumName(String stadiumName) {
		this.stadiumName = stadiumName;
	}

	public int getTerraces() {
		return terraces;
	}

	public void setTerraces(int terraces) {
		this.terraces = terraces;
	}

	public int getBasicSeating() {
		return basicSeating;
	}

	public void setBasicSeating(int basicSeating) {
		this.basicSeating = basicSeating;
	}

	public int getUnderRoofSeating() {
		return underRoofSeating;
	}

	public void setUnderRoofSeating(int underRoofSeating) {
		this.underRoofSeating = underRoofSeating;
	}

	public int getVipBox() {
		return vipBox;
	}

	public void setVipBox(int vipBox) {
		this.vipBox = vipBox;
	}

	public boolean isUnderConstruction() {
		return underConstruction;
	}

	public void setUnderConstruction(boolean underConstruction) {
		this.underConstruction = underConstruction;
	}

	public int getTerracesUnderConstruction() {
		return terracesUnderConstruction;
	}

	public void setTerracesUnderConstruction(int terracesUnderConstruction) {
		this.terracesUnderConstruction = terracesUnderConstruction;
	}

	public int getBasicSeatingUnderConstruction() {
		return basicSeatingUnderConstruction;
	}

	public void setBasicSeatingUnderConstruction(int basicSeatingUnderConstruction) {
		this.basicSeatingUnderConstruction = basicSeatingUnderConstruction;
	}

	public int getUnderRoofSeatingUnderConstruction() {
		return underRoofSeatingUnderConstruction;
	}

	public void setUnderRoofSeatingUnderConstruction(int underRoofSeatingUnderConstruction) {
		this.underRoofSeatingUnderConstruction = underRoofSeatingUnderConstruction;
	}

	public int getVipBoxUnderConstruction() {
		return vipBoxUnderConstruction;
	}

	public void setVipBoxUnderConstruction(int vipBoxUnderConstruction) {
		this.vipBoxUnderConstruction = vipBoxUnderConstruction;
	}

	public AmountOfMoney getExpansionCosts() {
		return expansionCosts;
	}

	public void setExpansionCosts(AmountOfMoney expansionCosts) {
		this.expansionCosts = expansionCosts;
	}

	public HODateTime getRebuiltDate() {
		return rebuiltDate;
	}

	public void setRebuiltDate(HODateTime rebuiltDate) {
		this.rebuiltDate = rebuiltDate;
	}

	public HODateTime getExpansionDate() {
		return expansionDate;
	}

	public void setExpansionDate(HODateTime expansionDate) {
		this.expansionDate = expansionDate;
	}
}
