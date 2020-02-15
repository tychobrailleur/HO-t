package module.series.promotion;

import com.google.gson.Gson;
import core.db.DBManager;
import core.file.xml.TeamStats;
import core.file.xml.XMLLeagueDetailsParser;
import core.file.xml.XMLTeamDetailsParser;
import core.gui.model.UserColumnController;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.net.MyConnector;
import core.util.HOLogger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class DownloadCountryDetails {


    static class CountryStructure {
        String name;
        int id;
        int[] leagueStructure;

        CountryStructure(String name, int id, int[] leagueStructure) {
            this.name = name;
            this.id = id;
            this.leagueStructure = leagueStructure;
        }
    }

    private final static Map<Integer, CountryStructure> COUNTRIES = new HashMap<>();
    private final static int[] LEAGUE_SIZES = new int[]{1, 4, 16, 64, 256, 1_024, 1_024};


    static {
        COUNTRIES.put(5, new CountryStructure("France", 5, new int[]{703, 704, 708, 5_450, 21_577, 35_989}));
        COUNTRIES.put(8, new CountryStructure("USA", 8, new int[]{597, 598, 602, 618, 8_374}));
        COUNTRIES.put(12, new CountryStructure("Finland", 12, new int[]{2_280, 2281, 2285, 2301, 8839, 31069}));
        COUNTRIES.put(21, new CountryStructure("Ireland", 21, new int[]{3573, 3574, 3578, 8775, 34583}));
    }

    final private ExecutorService executorService = Executors.newFixedThreadPool(5);
    final MyConnector mc = MyConnector.instance();

    private int getTeamRank(int teamId) {
        HOLogger.instance().info(DownloadCountryDetails.class, String.format("Retrieving Team details for team %d.",teamId));

        try {
            String details = mc.getTeamdetails(teamId);
            Map<String, String> teamInfo = XMLTeamDetailsParser.parseTeamdetailsFromString(details, teamId);

            return Integer.parseInt(teamInfo.getOrDefault("TeamRank", "-1"));

        } catch (IOException e) {
            HOLogger.instance().log(DownloadCountryDetails.class, e);
        }

        return -1;
    }


    private Map<String, TeamStats> getTeamsInfoInLeague(int leagueId) {
        String details = mc.getLeagueDetails(String.valueOf(leagueId));

//        String details = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
//                "<HattrickData>\n" +
//                "  <FileName>leaguedetails.xml</FileName>\n" +
//                "  <Version>1.2</Version>\n" +
//                "  <UserID>6992417</UserID>\n" +
//                "  <FetchedDate>2020-02-15 11:37:05</FetchedDate>\n" +
//                "  <LeagueID>21</LeagueID>\n" +
//                "  <LeagueName>Ireland</LeagueName>\n" +
//                "  <LeagueLevel>1</LeagueLevel>\n" +
//                "  <MaxLevel>5</MaxLevel>\n" +
//                "  <LeagueLevelUnitID>3573</LeagueLevelUnitID>\n" +
//                "  <LeagueLevelUnitName>Irish Premier</LeagueLevelUnitName>\n" +
//                "  <Team>\n" +
//                "    <TeamID>73868</TeamID>\n" +
//                "    <TeamName>OLLImpique Offaly</TeamName>\n" +
//                "    <Position>1</Position>\n" +
//                "    <PositionChange>0</PositionChange>\n" +
//                "    <Matches>0</Matches>\n" +
//                "    <GoalsFor>0</GoalsFor>\n" +
//                "    <GoalsAgainst>0</GoalsAgainst>\n" +
//                "    <Points>0</Points>\n" +
//                "  </Team>\n" +
//                "  <Team>\n" +
//                "    <TeamID>280009</TeamID>\n" +
//                "    <TeamName>Smithwick's FC</TeamName>\n" +
//                "    <Position>2</Position>\n" +
//                "    <PositionChange>0</PositionChange>\n" +
//                "    <Matches>0</Matches>\n" +
//                "    <GoalsFor>0</GoalsFor>\n" +
//                "    <GoalsAgainst>0</GoalsAgainst>\n" +
//                "    <Points>0</Points>\n" +
//                "  </Team>\n" +
//                "  <Team>\n" +
//                "    <TeamID>280318</TeamID>\n" +
//                "    <TeamName>F.C. Spórtcairde Knockpasheemore</TeamName>\n" +
//                "    <Position>3</Position>\n" +
//                "    <PositionChange>0</PositionChange>\n" +
//                "    <Matches>0</Matches>\n" +
//                "    <GoalsFor>0</GoalsFor>\n" +
//                "    <GoalsAgainst>0</GoalsAgainst>\n" +
//                "    <Points>0</Points>\n" +
//                "  </Team>\n" +
//                "  <Team>\n" +
//                "    <TeamID>281438</TeamID>\n" +
//                "    <TeamName>Sligo Bay Crabs</TeamName>\n" +
//                "    <Position>4</Position>\n" +
//                "    <PositionChange>0</PositionChange>\n" +
//                "    <Matches>0</Matches>\n" +
//                "    <GoalsFor>0</GoalsFor>\n" +
//                "    <GoalsAgainst>0</GoalsAgainst>\n" +
//                "    <Points>0</Points>\n" +
//                "  </Team>\n" +
//                "  <Team>\n" +
//                "    <TeamID>281450</TeamID>\n" +
//                "    <TeamName>Losers of CA</TeamName>\n" +
//                "    <Position>5</Position>\n" +
//                "    <PositionChange>0</PositionChange>\n" +
//                "    <Matches>0</Matches>\n" +
//                "    <GoalsFor>0</GoalsFor>\n" +
//                "    <GoalsAgainst>0</GoalsAgainst>\n" +
//                "    <Points>0</Points>\n" +
//                "  </Team>\n" +
//                "  <Team>\n" +
//                "    <TeamID>281662</TeamID>\n" +
//                "    <TeamName>The Snuggly Duckling</TeamName>\n" +
//                "    <Position>6</Position>\n" +
//                "    <PositionChange>0</PositionChange>\n" +
//                "    <Matches>0</Matches>\n" +
//                "    <GoalsFor>0</GoalsFor>\n" +
//                "    <GoalsAgainst>0</GoalsAgainst>\n" +
//                "    <Points>0</Points>\n" +
//                "  </Team>\n" +
//                "  <Team>\n" +
//                "    <TeamID>280396</TeamID>\n" +
//                "    <TeamName>McVignes</TeamName>\n" +
//                "    <Position>7</Position>\n" +
//                "    <PositionChange>0</PositionChange>\n" +
//                "    <Matches>0</Matches>\n" +
//                "    <GoalsFor>0</GoalsFor>\n" +
//                "    <GoalsAgainst>0</GoalsAgainst>\n" +
//                "    <Points>0</Points>\n" +
//                "  </Team>\n" +
//                "  <Team>\n" +
//                "    <TeamID>279869</TeamID>\n" +
//                "    <TeamName>3Dimaina</TeamName>\n" +
//                "    <Position>8</Position>\n" +
//                "    <PositionChange>0</PositionChange>\n" +
//                "    <Matches>0</Matches>\n" +
//                "    <GoalsFor>0</GoalsFor>\n" +
//                "    <GoalsAgainst>0</GoalsAgainst>\n" +
//                "    <Points>0</Points>\n" +
//                "  </Team>\n" +
//                "</HattrickData>";
        return XMLLeagueDetailsParser.parseLeagueDetails(details);

    }

    /**
     * Retrieves all the teams in the country of id <code>countryId</code>
     *
     * @param countryId – ID of the country for which we are getting all the teams.
     */
    public void getTeamsInCountry(int countryId) {
        CountryStructure structure = COUNTRIES.get(countryId);

        Map<String, TeamStats> teamsInfo = new ConcurrentHashMap<>();

        final Queue<Integer> queue = new LinkedBlockingQueue<>();
        for (int i = 0; i < structure.leagueStructure.length; i++) {
            int leagueSize = LEAGUE_SIZES[i];

            for (int leagueId = structure.leagueStructure[i]; leagueId < structure.leagueStructure[i] + leagueSize; leagueId++) {
//                int currentLeagueId = queue.poll();
                final int currentLeagueId = leagueId;
                Map<String, TeamStats> teamsInfoInLeague = getTeamsInfoInLeague(currentLeagueId);
                System.out.println(teamsInfoInLeague);
                teamsInfo.putAll(teamsInfoInLeague);
            }
        }

        HOLogger.instance().info(DownloadCountryDetails.class, String.format("Found %d teams.", teamsInfo.size()));

        CountryTeamInfo countryTeamInfo = new CountryTeamInfo();
        countryTeamInfo.countryId = countryId;

        Map<Integer, CountryTeamInfo.TeamRank> teamRankMap = new HashMap<>();

        for (TeamStats teamStats : teamsInfo.values()) {
            teamRankMap.put(teamStats.getTeamId(), new CountryTeamInfo.TeamRank(teamStats.getTeamId(), teamStats.rankingScore()));
        }

        countryTeamInfo.ranks.addAll(new ArrayList<>(teamRankMap.values()));
        countryTeamInfo.ranks.sort(Comparator.comparingLong(o -> -o.rank));

        handleDuplicateRankings(countryTeamInfo, teamRankMap);

        Gson gson = new Gson();
        String json = gson.toJson(countryTeamInfo);

        System.out.println(json);
    }

    private void handleDuplicateRankings(CountryTeamInfo countryTeamInfo, Map<Integer, CountryTeamInfo.TeamRank> teamRankMap) {
    /*
    countryTeamInfo.ranks = Arrays.asList(
            new CountryTeamInfo.TeamRank(1, 43),
            new CountryTeamInfo.TeamRank(2, 44),
            new CountryTeamInfo.TeamRank(3, 78),
            new CountryTeamInfo.TeamRank(4, 78),
            new CountryTeamInfo.TeamRank(5, 79),
            new CountryTeamInfo.TeamRank(6, 43)
    );

     */

        List<CountryTeamInfo.TeamRank> duplicateRanks = countryTeamInfo.ranks
               .stream()
               .collect(Collectors.groupingBy(CountryTeamInfo.TeamRank::getRank))
               .entrySet()
               .stream()
               .filter(longListEntry -> longListEntry.getValue().size() > 1) // filter out ranks that appear once
               .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .entrySet()
                .stream()
                .flatMap(longListEntry -> longListEntry.getValue().stream())
                .collect(Collectors.toList()); // merge all the lists of team ranks

        System.out.println(duplicateRanks);

        countryTeamInfo.ranks.clear();
        for (CountryTeamInfo.TeamRank rank: duplicateRanks) {
            int observedRank = getTeamRank(rank.teamId);
            CountryTeamInfo.TeamRank teamRank = teamRankMap.get(rank.teamId);
            teamRank.setRank(teamRank.getRank() + (99_999 - observedRank));
            teamRankMap.put(rank.teamId, teamRank);
        }

        countryTeamInfo.ranks.addAll(new ArrayList<>(teamRankMap.values()));
        countryTeamInfo.ranks.sort(Comparator.comparingLong(o -> -o.rank));
    }

    public static void main(String[] args) {
        DBManager.instance().loadUserParameter();
        HOVerwaltung.checkLanguageFile(UserParameter.instance().sprachDatei);
        HOVerwaltung.instance().setResource(UserParameter.instance().sprachDatei);
        HOVerwaltung.instance().loadLatestHoModel();
        UserColumnController.instance().load();

        DownloadCountryDetails countryDetails = new DownloadCountryDetails();
        countryDetails.getTeamsInCountry(21);
        System.exit(1);
    }
}
