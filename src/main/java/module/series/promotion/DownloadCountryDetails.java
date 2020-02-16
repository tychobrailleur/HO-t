package module.series.promotion;

import com.google.gson.Gson;
import core.file.xml.TeamStats;
import core.file.xml.XMLLeagueDetailsParser;
import core.file.xml.XMLTeamDetailsParser;
import core.model.HOVerwaltung;
import core.net.MyConnector;
import core.util.HOLogger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * This class gets all the teams in a given country, calculates the ranking for each one of them,
 * and submits the data back to the HO server.
 *
 * <p>The ranking for a team is calculated as follows:</p>
 *
 * ...
 *
 * <p>When a user wants to check what league he or she will be promoted/demoted to the following season,
 * HO checks whether the ranking calculation has already been done for his/her country.  If it has not been
 * done already, HO downloads the data for his/her country, calculates the ranking, and submits the
 * results back to the HO server: this first user is responsible for seeding the data for his/her country.</p>
 *
 * <p>when the data is being downloaded for the country, an endpoint on HO server is called to mark
 * the data retrieval for that country as pending.  While the data retrieval is pending, no one else can
 * download the data for this country.  This means that if the user interrupts the download (for example
 * by shutting down HO prematurely, or because of a crash), the status of data retrieval for this country
 * needs to be reset to “unavailable” after a certain period of time.</p>
 *
 * <p>Once the data for a country is available, the data request to the HO server will return the pre-calculated
 * info without the need for downloading more data from HT.</p>
 *
 * <p>TODO Solve the following problems:
 * <ul>
 *     <li>How to secure the endpoint where country data is submitted?</li>
 *     <li>How to make sure the data submitted by the user is not garbage?</li>
 * </ul>
 */
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

    final MyConnector mc = MyConnector.instance();
    final DataSubmitter submitter = new HttpDataSubmitter();

    /**
     * Retrieves all the teams in the country of id <code>countryId</code>.
     *
     * @param countryId – ID of the country for which we are getting all the teams.
     */
    public void getTeamsInCountry(int countryId) {
        int season = HOVerwaltung.instance().getModel().getBasics().getSeason();
        String username = HOVerwaltung.instance().getModel().getBasics().getManager();

        CountryTeamInfo countryTeamInfo = new CountryTeamInfo();
        countryTeamInfo.leagueId = countryId;
        countryTeamInfo.season = season;
        countryTeamInfo.username = username;

        Map<Integer, CountryTeamInfo.TeamRank> teamRankMap = getCountryTeamsRanking(countryId, countryTeamInfo);
        handleDuplicateRankings(countryTeamInfo, teamRankMap);
        createJson(countryTeamInfo);
    }

    private Map<Integer, CountryTeamInfo.TeamRank> getCountryTeamsRanking(int countryId, CountryTeamInfo countryTeamInfo) {
        CountryStructure structure = COUNTRIES.get(countryId);

        Map<String, TeamStats> teamsInfo = new ConcurrentHashMap<>();
        ProcessAsynchronousTask<Integer> processAsynchronousTask = new ProcessAsynchronousTask<>();

        // Find all the series in the league, and them to the processing queue.
        for (int i = 0; i < structure.leagueStructure.length; i++) {
            int leagueSize = LEAGUE_SIZES[i];

            for (int leagueId = structure.leagueStructure[i]; leagueId < structure.leagueStructure[i] + leagueSize; leagueId++) {
                processAsynchronousTask.addToQueue(leagueId);
            }
        }

        ProcessAsynchronousTask.ProcessTask<Integer> task = (val) -> {
            Map<String, TeamStats> teamsInfoInSeries = getTeamsInfoInSeries(val);
            System.out.println(teamsInfoInSeries);
            teamsInfo.putAll(teamsInfoInSeries);
        };
        processAsynchronousTask.execute(task);

        HOLogger.instance().info(DownloadCountryDetails.class, String.format("Found %d teams.", teamsInfo.size()));

        Map<Integer, CountryTeamInfo.TeamRank> teamRankMap = teamsInfo.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getValue().getTeamId(),
                        e -> new CountryTeamInfo.TeamRank(e.getValue().getTeamId(), e.getValue().rankingScore())));

        // sort ranks, it will be used to find ex-aequo teams.
        countryTeamInfo.calculatedRank.addAll(new ArrayList<>(teamRankMap.values()));
        countryTeamInfo.calculatedRank.sort(Comparator.comparingLong(o -> -o.calculatedRank));

        return teamRankMap;
    }

    private Map<String, TeamStats> getTeamsInfoInSeries(int leagueId) {
        String details = mc.getLeagueDetails(String.valueOf(leagueId));
        return XMLLeagueDetailsParser.parseLeagueDetails(details);
    }

    private int getTeamRank(int teamId) {
        HOLogger.instance().info(DownloadCountryDetails.class, String.format("Retrieving Team details for team %d.", teamId));

        try {
            String details = mc.getTeamdetails(teamId);
            Map<String, String> teamInfo = XMLTeamDetailsParser.parseTeamdetailsFromString(details, teamId);

            return Integer.parseInt(teamInfo.getOrDefault("TeamRank", "-1"));

        } catch (IOException e) {
            HOLogger.instance().log(DownloadCountryDetails.class, e);
        }

        return -1;
    }

    private void handleDuplicateRankings(CountryTeamInfo countryTeamInfo, Map<Integer, CountryTeamInfo.TeamRank> teamRankMap) {
        // Find ranks for which we have duplicate ranks.
        List<CountryTeamInfo.TeamRank> duplicateRanks = countryTeamInfo.calculatedRank
                .stream()
                .collect(Collectors.groupingBy(CountryTeamInfo.TeamRank::getCalculatedRank))
                .entrySet()
                .stream()
                .filter(longListEntry -> longListEntry.getValue().size() > 1) // filter out ranks that appear once
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .entrySet()
                .stream()
                .flatMap(longListEntry -> longListEntry.getValue().stream())
                .collect(Collectors.toList()); // merge all the lists of team ranks

        HOLogger.instance().info(DownloadCountryDetails.class, String.format("Found %d team with duplicate ranks.", duplicateRanks.size()));
        countryTeamInfo.calculatedRank.clear();

        ProcessAsynchronousTask<Integer> processAsynchronousTask = new ProcessAsynchronousTask<>();

        for (CountryTeamInfo.TeamRank rank : duplicateRanks) {
            processAsynchronousTask.addToQueue(rank.teamId);
        }

        ProcessAsynchronousTask.ProcessTask<Integer> task = (val) -> {
            int observedRank = getTeamRank(val);
            CountryTeamInfo.TeamRank teamRank = teamRankMap.get(val);
            teamRank.setCalculatedRank(teamRank.getCalculatedRank() + (99_999 - observedRank));
            teamRankMap.put(val, teamRank);
        };
        processAsynchronousTask.execute(task);

        countryTeamInfo.calculatedRank.addAll(new ArrayList<>(teamRankMap.values()));
        countryTeamInfo.calculatedRank.sort(Comparator.comparingLong(o -> -o.calculatedRank));
    }

    private void createJson(CountryTeamInfo countryTeamInfo) {
        Gson gson = new Gson();
        String json = gson.toJson(countryTeamInfo);
        HOLogger.instance().debug(DownloadCountryDetails.class, json);

        submitter.submitData(json);
    }
}
