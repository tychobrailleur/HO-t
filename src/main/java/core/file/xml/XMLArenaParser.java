package core.file.xml;

import core.util.HODateTime;
import core.util.HOLogger;
import hattrickdata.*;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Optional;

public class XMLArenaParser {

    private static final String ELEMENT_NAME_FILE_NAME = "FileName";
    private static final String ELEMENT_NAME_VERSION = "Version";
    private static final String ELEMENT_NAME_USER_ID = "UserID";
    private static final String ELEMENT_NAME_FETCHED_DATE = "FetchedDate";
    private static final String ELEMENT_NAME_ARENA = "Arena";
    private static final String ELEMENT_NAME_ARENA_ID = "ArenaID";
    private static final String ELEMENT_NAME_ARENA_NAME = "ArenaName";
    private static final String ELEMENT_NAME_TEAM = "Team";
    private static final String ELEMENT_NAME_TEAM_ID = "TeamID";
    private static final String ELEMENT_NAME_TEAM_NAME = "TeamName";
    private static final String ELEMENT_NAME_LEAGUE = "League";
    private static final String ELEMENT_NAME_LEAGUE_ID = "LeagueID";
    private static final String ELEMENT_NAME_LEAGUE_NAME = "LeagueName";
    private static final String ELEMENT_NAME_REGION = "Region";
    private static final String ELEMENT_NAME_REGION_ID = "RegionID";
    private static final String ELEMENT_NAME_REGION_NAME = "RegionName";
    private static final String ELEMENT_NAME_CURRENT_CAPACITY = "CurrentCapacity";
    private static final String ELEMENT_NAME_CURRENT_CAPACITY_REBUILT_DATE = "RebuiltDate";

    private static final String ELEMENT_NAME_EXPANDED_CAPACITY = "ExpandedCapacity";
    private static final String ELEMENT_NAME_EXPANDED_CAPACITY_EXPANSION_DATE = "ExpansionDate";

    private static final String ATTRIBUTE_NAME_CAPACITY_AVAILABLE = "Available";
    private static final String ELEMENT_NAME_CAPACITY_TERRACES = "Terraces";
    private static final String ELEMENT_NAME_CAPACITY_BASIC = "Basic";
    private static final String ELEMENT_NAME_CAPACITY_ROOF = "Roof";
    private static final String ELEMENT_NAME_CAPACITY_VIP = "VIP";
    private static final String ELEMENT_NAME_CAPACITY_TOTAL = "Total";

    private XMLArenaParser() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Pair<HattrickDataInfo, Arena> parseArenaFromString(String str) {
        return parseDetails(XMLManager.parseString(str));
    }

    private static Pair<HattrickDataInfo, Arena> parseDetails(Document doc) {
        if (doc == null) {
            return null;
        }

        try {
            Element root = doc.getDocumentElement();

            // FileName
            Element element = (Element) root.getElementsByTagName(ELEMENT_NAME_FILE_NAME).item(0);
            String fileName = XMLManager.getFirstChildNodeValue(element);
            // Version
            element = (Element) root.getElementsByTagName(ELEMENT_NAME_VERSION).item(0);
            String version = XMLManager.getFirstChildNodeValue(element);
            // UserId
            element = (Element) root.getElementsByTagName(ELEMENT_NAME_USER_ID).item(0);
            int userId = Integer.parseInt(XMLManager.getFirstChildNodeValue(element));
            // FetchedDate
            element = (Element) root.getElementsByTagName(ELEMENT_NAME_FETCHED_DATE).item(0);
            HODateTime fetchedDate = HODateTime.fromHT(XMLManager.getFirstChildNodeValue(element));

            final var hattrickDataInfo = new HattrickDataInfo(fileName, version, userId, fetchedDate);

            // Root wechseln
            root = (Element) root.getElementsByTagName(ELEMENT_NAME_ARENA).item(0);
            element = (Element) root.getElementsByTagName(ELEMENT_NAME_ARENA_ID).item(0);

            int arenaId = Integer.parseInt(XMLManager.getFirstChildNodeValue(element));
            element = (Element) root.getElementsByTagName(ELEMENT_NAME_ARENA_NAME).item(0);
            String arenaName = XMLManager.getFirstChildNodeValue(element);

            // Team
            Element tmpRoot = (Element) root.getElementsByTagName(ELEMENT_NAME_TEAM).item(0);
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_TEAM_ID).item(0);
            int id = Integer.parseInt(XMLManager.getFirstChildNodeValue(element));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_TEAM_NAME).item(0);
            String name = XMLManager.getFirstChildNodeValue(element);
            TeamIdName teamIdName = new TeamIdName(id, name);

            // League
            tmpRoot = (Element) root.getElementsByTagName(ELEMENT_NAME_LEAGUE).item(0);
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_LEAGUE_ID).item(0);
            id = Integer.parseInt(XMLManager.getFirstChildNodeValue(element));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_LEAGUE_NAME).item(0);
            name = XMLManager.getFirstChildNodeValue(element);
            LeagueIdName leagueIdName = new LeagueIdName(id, name);

            // Region
            tmpRoot = (Element) root.getElementsByTagName(ELEMENT_NAME_REGION).item(0);
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_REGION_ID).item(0);
            id = Integer.parseInt(XMLManager.getFirstChildNodeValue(element));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_REGION_NAME).item(0);
            name = XMLManager.getFirstChildNodeValue(element);
            RegionIdName regionIdName = new RegionIdName(id, name);

            // Current Capacity
            tmpRoot = (Element) root.getElementsByTagName(ELEMENT_NAME_CURRENT_CAPACITY).item(0);
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CURRENT_CAPACITY_REBUILT_DATE).item(0);

            final boolean rebuiltDateAvailable = getXmlAttributeAsBoolean(element, ATTRIBUTE_NAME_CAPACITY_AVAILABLE);
            HODateTime rebuildDate = null;
            if (rebuiltDateAvailable) {
                rebuildDate = HODateTime.fromHT(XMLManager.getFirstChildNodeValue(element));
            }

            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_TERRACES).item(0);
            int terraces = Integer.parseInt(XMLManager.getFirstChildNodeValue(element));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_BASIC).item(0);
            int basic = Integer.parseInt(XMLManager.getFirstChildNodeValue(element));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_ROOF).item(0);
            int roof = Integer.parseInt(XMLManager.getFirstChildNodeValue(element));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_VIP).item(0);
            int vip = Integer.parseInt(XMLManager.getFirstChildNodeValue(element));
            element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_TOTAL).item(0);
            int total = Integer.parseInt(XMLManager.getFirstChildNodeValue(element));

            Capacity currentCapacity = new Capacity(terraces, basic, roof, vip, total, rebuildDate, null);

            tmpRoot = (Element) root.getElementsByTagName(ELEMENT_NAME_EXPANDED_CAPACITY).item(0);

            final boolean expandedCapacityAvailable = getXmlAttributeAsBoolean(tmpRoot,
                    ATTRIBUTE_NAME_CAPACITY_AVAILABLE);
            Capacity expandedCapacity = null;
            if (expandedCapacityAvailable) {
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_EXPANDED_CAPACITY_EXPANSION_DATE).item(0);
                HODateTime expansionDate = HODateTime.fromHT(XMLManager.getFirstChildNodeValue(element));
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_TERRACES).item(0);
                terraces = Integer.parseInt(XMLManager.getFirstChildNodeValue(element));
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_BASIC).item(0);
                basic = Integer.parseInt(XMLManager.getFirstChildNodeValue(element));
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_ROOF).item(0);
                roof = Integer.parseInt(XMLManager.getFirstChildNodeValue(element));
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_VIP).item(0);
                vip = Integer.parseInt(XMLManager.getFirstChildNodeValue(element));
                element = (Element) tmpRoot.getElementsByTagName(ELEMENT_NAME_CAPACITY_TOTAL).item(0);
                total = Integer.parseInt(XMLManager.getFirstChildNodeValue(element));

                expandedCapacity = new Capacity(terraces, basic, roof, vip, total, null, expansionDate);
            }

            Arena arena = new Arena(arenaId, arenaName, teamIdName, leagueIdName, regionIdName, currentCapacity, Optional.ofNullable(expandedCapacity));

            return Pair.of(hattrickDataInfo, arena);
        } catch (Exception e) {
            HOLogger.instance().log(XMLArenaParser.class, e);
        }

        return null;
    }

    private static boolean getXmlAttributeAsBoolean(Element element, String attributeName) {
        return Boolean.parseBoolean(XMLManager.getAttributeValue(element, attributeName).trim());
    }
}
