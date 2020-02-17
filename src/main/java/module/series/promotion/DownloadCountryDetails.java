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
        COUNTRIES.put(98, new CountryStructure("Albania", 98, new int[] { 88340, 88346, 88350, 208326, 252057 }));
        COUNTRIES.put(118, new CountryStructure("Algeria", 118, new int[] { 123069, 123070, 123074, 225756, 252975 }));
        COUNTRIES.put(105, new CountryStructure("Andorra", 105, new int[] { 88385, 88391, 88415, 238619 }));
        COUNTRIES.put(130, new CountryStructure("Angola", 130, new int[] { 209686, 209688, 209692 }));
        COUNTRIES.put(7, new CountryStructure("Argentina", 7, new int[] { 342, 343, 347, 363, 13220, 26313 }));
        COUNTRIES.put(122, new CountryStructure("Armenia", 122, new int[] { 123133, 123138, 123158 }));
        COUNTRIES.put(39, new CountryStructure("Austria", 39, new int[] { 4205, 8755, 8759, 9639, 17220, 37013 }));
        COUNTRIES.put(129, new CountryStructure("Azerbaijan", 129, new int[] { 201137, 201138, 201142, 238555 }));
        COUNTRIES.put(123, new CountryStructure("Bahrain", 123, new int[] { 123188, 123189, 123193, 253231 }));
        COUNTRIES.put(132, new CountryStructure("Bangladesh", 132, new int[] { 209729, 209730, 209734 }));
        COUNTRIES.put(124, new CountryStructure("Barbados", 124, new int[] { 123209, 123212, 123224 }));
        COUNTRIES.put(91, new CountryStructure("Belarus", 91, new int[] { 60146, 60152, 60240, 115240, 245615 }));
        COUNTRIES.put(44, new CountryStructure("Belgium", 44, new int[] { 8714, 8715, 8719, 9703, 12708, 22985 }));
        COUNTRIES.put(139, new CountryStructure("Benin", 139, new int[] { 238747, 238749, 238757 }));
        COUNTRIES.put(74, new CountryStructure("Bolivia", 74, new int[] { 34840, 34866, 34901, 48704, 66096 }));
        COUNTRIES.put(69, new CountryStructure("Bosnia and Herzegovina", 69, new int[] { 29726, 29727, 29731, 48768, 66352 }));
        COUNTRIES.put(16, new CountryStructure("Brazil", 16, new int[] { 3229, 3230, 3234, 3250, 11047, 52757 }));
        COUNTRIES.put(136, new CountryStructure("Brunei", 136, new int[] { 229917, 229918, 229926 }));
        COUNTRIES.put(62, new CountryStructure("Bulgaria", 62, new int[] { 14234, 14235, 14239, 18953, 65840 }));
        COUNTRIES.put(138, new CountryStructure("Cambodia", 138, new int[] { 237126, 237127, 237131 }));
        COUNTRIES.put(146, new CountryStructure("Cameroon", 146, new int[] { 252313, 252317, 252321 }));
        COUNTRIES.put(17, new CountryStructure("Canada", 17, new int[] { 3314, 3315, 3319, 8630, 12964 }));
        COUNTRIES.put(125, new CountryStructure("Cape Verde", 125, new int[] { 123210, 123216, 123240 }));
        COUNTRIES.put(18, new CountryStructure("Chile", 18, new int[] { 3335, 3336, 3340, 28361, 33351, 34965 }));
        COUNTRIES.put(60, new CountryStructure("Chinese Taipei", 60, new int[] { 13531, 13532, 13536, 98452, 116520 }));
        COUNTRIES.put(19, new CountryStructure("Colombia", 19, new int[] { 3377, 3378, 3382, 33287, 34071 }));
        COUNTRIES.put(151, new CountryStructure("Comoros", 151, new int[] { 258136, 258137, 258141, 258157 }));
        COUNTRIES.put(81, new CountryStructure("Costa Rica", 81, new int[] { 56879, 56881, 56889, 76288 }));
        COUNTRIES.put(126, new CountryStructure("Côte d’Ivoire", 126, new int[] { 123211, 123220, 123256 }));
        COUNTRIES.put(58, new CountryStructure("Croatia", 58, new int[] { 11387, 11388, 11392, 14255, 19017 }));
        COUNTRIES.put(147, new CountryStructure("Cuba", 147, new int[] { 252358, 252359, 252383 }));
        COUNTRIES.put(153, new CountryStructure("Curaçao", 153, new int[] { 258115, 258116, 258120, 258221 }));
        COUNTRIES.put(89, new CountryStructure("Cyprus", 89, new int[] { 57560, 57561, 57565, 115368, 218006 }));
        COUNTRIES.put(52, new CountryStructure("Czech Republic", 52, new int[] { 11303, 11304, 11308, 14085, 59629, 111144 }));
        COUNTRIES.put(11, new CountryStructure("Denmark", 11, new int[] { 1769, 1770, 1774, 1790, 1854, 24009 }));
        COUNTRIES.put(88, new CountryStructure("Dominican Republic", 88, new int[] { 57539, 57540, 57544 }));
        COUNTRIES.put(155, new CountryStructure("DR Congo", 155, new int[] { 258477, 258478, 258482 }));
        COUNTRIES.put(73, new CountryStructure("Ecuador", 73, new int[] { 34841, 34862, 34885, 116840 }));
        COUNTRIES.put(33, new CountryStructure("Egypt", 33, new int[] { 3398, 3399, 9367, 57434, 238171 }));
        COUNTRIES.put(100, new CountryStructure("El Salvador", 100, new int[] { 88256, 88260, 88292, 238791 }));
        COUNTRIES.put(2, new CountryStructure("England", 2, new int[] { 512, 513, 517, 533, 6348, 6604 }));
        COUNTRIES.put(56, new CountryStructure("Estonia", 56, new int[] { 11366, 11367, 11371, 11492, 13829 }));
        COUNTRIES.put(156, new CountryStructure("Ethiopia", 156, new int[] { 258498, 258499, 258503 }));
        COUNTRIES.put(76, new CountryStructure("Faroe Islands", 76, new int[] { 34871, 34877, 34933, 48832, 208406 }));
        COUNTRIES.put(12, new CountryStructure("Finland", 12, new int[] { 2280, 2281, 2285, 2301, 8839, 31069 }));
        COUNTRIES.put(5, new CountryStructure("France", 5, new int[] { 703, 704, 708, 5450, 21577, 35989 }));
        COUNTRIES.put(104, new CountryStructure("Georgia", 104, new int[] { 88382, 88386, 88431, 238683, 249113 }));
        COUNTRIES.put(3, new CountryStructure("Germany", 3, new int[] { 427, 428, 432, 448, 6092, 15343, 41109 }));
        COUNTRIES.put(137, new CountryStructure("Ghana", 137, new int[] { 229916, 229922, 229942 }));
        COUNTRIES.put(50, new CountryStructure("Greece", 50, new int[] { 11345, 11346, 11350, 13765, 34327 }));
        COUNTRIES.put(154, new CountryStructure("Guam", 154, new int[] { 258073, 258074, 258078, 258413 }));
        COUNTRIES.put(107, new CountryStructure("Guatemala", 107, new int[] { 88447, 88448, 88452, 203462 }));
        COUNTRIES.put(1000, new CountryStructure("Hattrick International", 1000, new int[] { 256687, 256688, 256692, 256708, 256772, 257028 }));
        COUNTRIES.put(99, new CountryStructure("Honduras", 99, new int[] { 88257, 88264, 88276, 256495 }));
        COUNTRIES.put(59, new CountryStructure("Hong Kong", 59, new int[] { 13508, 13509, 13513, 13616, 98516 }));
        COUNTRIES.put(51, new CountryStructure("Hungary", 51, new int[] { 11324, 11325, 11329, 11556, 19273, 71792 }));
        COUNTRIES.put(38, new CountryStructure("Iceland", 38, new int[] { 4200, 4201, 8038, 18500 }));
        COUNTRIES.put(20, new CountryStructure("India", 20, new int[] { 3488, 3489, 3493, 3509, 249689 }));
        COUNTRIES.put(54, new CountryStructure("Indonesia", 54, new int[] { 11408, 11409, 11413, 13701, 57177 }));
        COUNTRIES.put(85, new CountryStructure("Iran", 85, new int[] { 57518, 57519, 57523, 203526, 239151 }));
        COUNTRIES.put(128, new CountryStructure("Iraq", 128, new int[] { 200092, 200093, 200097, 252463 }));
        COUNTRIES.put(21, new CountryStructure("Ireland", 21, new int[] { 3573, 3574, 3578, 8775, 34583 }));
        COUNTRIES.put(63, new CountryStructure("Israel", 63, new int[] { 13680, 13681, 13685, 18569, 18633, 42155 }));
        COUNTRIES.put(4, new CountryStructure("Italy", 4, new int[] { 724, 725, 729, 5772, 5836, 28702, 67632, 89940 }));
        COUNTRIES.put(94, new CountryStructure("Jamaica", 94, new int[] { 60148, 60160, 60224, 249049 }));
        COUNTRIES.put(22, new CountryStructure("Japan", 22, new int[] { 3594, 3595, 3599, 65456, 117160 }));
        COUNTRIES.put(106, new CountryStructure("Jordan", 106, new int[] { 88390, 88395, 88399, 252847 }));
        COUNTRIES.put(112, new CountryStructure("Kazakhstan", 112, new int[] { 98814, 98815, 98819, 238427 }));
        COUNTRIES.put(95, new CountryStructure("Kenya", 95, new int[] { 60149, 60164, 60208, 249625 }));
        COUNTRIES.put(127, new CountryStructure("Kuwait", 127, new int[] { 200087, 200088, 208390, 252527 }));
        COUNTRIES.put(102, new CountryStructure("Kyrgyzstan", 102, new int[] { 88341, 88342, 88366 }));
        COUNTRIES.put(53, new CountryStructure("Latvia", 53, new int[] { 11450, 11451, 11455, 14149, 28446 }));
        COUNTRIES.put(120, new CountryStructure("Lebanon", 120, new int[] { 123111, 123112, 123116, 253359 }));
        COUNTRIES.put(117, new CountryStructure("Liechtenstein", 117, new int[] { 123048, 123049, 123053, 203590 }));
        COUNTRIES.put(66, new CountryStructure("Lithuania", 66, new int[] { 29747, 29748, 29752, 33687, 59885 }));
        COUNTRIES.put(84, new CountryStructure("Luxembourg", 84, new int[] { 57433, 57498, 57502, 115304 }));
        COUNTRIES.put(45, new CountryStructure("Malaysia", 45, new int[] { 4213, 8735, 8739, 16900, 49429 }));
        COUNTRIES.put(144, new CountryStructure("Maldives", 144, new int[] { 245935, 245937, 245945 }));
        COUNTRIES.put(101, new CountryStructure("Malta", 101, new int[] { 88258, 88268, 88324, 88788, 203654 }));
        COUNTRIES.put(6, new CountryStructure("Mexico", 6, new int[] { 682, 683, 687, 2944, 25033 }));
        COUNTRIES.put(103, new CountryStructure("Moldova", 103, new int[] { 88259, 88272, 88308, 116776, 238863 }));
        COUNTRIES.put(119, new CountryStructure("Mongolia", 119, new int[] { 123090, 123091, 123095 }));
        COUNTRIES.put(131, new CountryStructure("Montenegro", 131, new int[] { 209708, 209709, 209713, 216918 }));
        COUNTRIES.put(77, new CountryStructure("Morocco", 77, new int[] { 34870, 34881, 34949, 208262, 252591 }));
        COUNTRIES.put(135, new CountryStructure("Mozambique", 135, new int[] { 225734, 225736, 225740 }));
        COUNTRIES.put(14, new CountryStructure("Netherlands", 14, new int[] { 2195, 2196, 2200, 2216, 8118, 17476 }));
        COUNTRIES.put(111, new CountryStructure("Nicaragua", 111, new int[] { 98793, 98794, 98798 }));
        COUNTRIES.put(75, new CountryStructure("Nigeria", 75, new int[] { 34872, 34873, 34917, 251993 }));
        COUNTRIES.put(97, new CountryStructure("North Macedonia", 97, new int[] { 60147, 60156, 60256, 115432, 218262 }));
        COUNTRIES.put(93, new CountryStructure("Northern Ireland", 93, new int[] { 60150, 60168, 60192, 88468 }));
        COUNTRIES.put(9, new CountryStructure("Norway", 9, new int[] { 2110, 2111, 2115, 2131, 7628, 19529 }));
        COUNTRIES.put(15, new CountryStructure("Oceania", 15, new int[] { 3208, 3209, 3213, 4214, 9095 }));
        COUNTRIES.put(134, new CountryStructure("Oman", 134, new int[] { 225713, 225714, 225718, 253423 }));
        COUNTRIES.put(71, new CountryStructure("Pakistan", 71, new int[] { 32093, 32094, 32098, 256431 }));
        COUNTRIES.put(148, new CountryStructure("Palestine", 148, new int[] { 252357, 252363, 252367, 252399 }));
        COUNTRIES.put(96, new CountryStructure("Panama", 96, new int[] { 60151, 60172, 60176, 76416, 116904 }));
        COUNTRIES.put(72, new CountryStructure("Paraguay", 72, new int[] { 42133, 42135, 42139, 76224, 203910 }));
        COUNTRIES.put(34, new CountryStructure("People's Republic of China", 34, new int[] { 3356, 3357, 3361, 13552, 98196 }));
        COUNTRIES.put(23, new CountryStructure("Peru", 23, new int[] { 3615, 3616, 13492, 33223, 49173 }));
        COUNTRIES.put(55, new CountryStructure("Philippines", 55, new int[] { 11429, 11430, 11434, 88852 }));
        COUNTRIES.put(24, new CountryStructure("Poland", 24, new int[] { 3620, 3621, 3625, 3641, 9383, 32114, 58605 }));
        COUNTRIES.put(25, new CountryStructure("Portugal", 25, new int[] { 3705, 3706, 3710, 3726, 9767, 10023 }));
        COUNTRIES.put(141, new CountryStructure("Qatar", 141, new int[] { 238789, 238855, 239119, 253295 }));
        COUNTRIES.put(37, new CountryStructure("Romania", 37, new int[] { 3854, 3855, 3859, 3875, 3939, 21961 }));
        COUNTRIES.put(35, new CountryStructure("Russia", 35, new int[] { 3187, 3188, 3192, 21897, 76480 }));
        COUNTRIES.put(149, new CountryStructure("São Tomé e Príncipe", 149, new int[] { 258094, 258095, 258099, 258285 }));
        COUNTRIES.put(79, new CountryStructure("Saudi Arabia", 79, new int[] { 48896, 48897, 48901, 245871, 253487 }));
        COUNTRIES.put(26, new CountryStructure("Scotland", 26, new int[] { 3166, 3167, 3171, 8054, 29789 }));
        COUNTRIES.put(121, new CountryStructure("Senegal", 121, new int[] { 123132, 123134, 123142, 256367 }));
        COUNTRIES.put(57, new CountryStructure("Serbia", 57, new int[] { 11471, 11472, 11476, 33751, 76736 }));
        COUNTRIES.put(47, new CountryStructure("Singapore", 47, new int[] { 4211, 4278, 4282, 4298, 16644 }));
        COUNTRIES.put(67, new CountryStructure("Slovakia", 67, new int[] { 29768, 29769, 29773, 65392, 88532 }));
        COUNTRIES.put(64, new CountryStructure("Slovenia", 64, new int[] { 14213, 14214, 14218, 18889, 65584 }));
        COUNTRIES.put(27, new CountryStructure("South Africa", 27, new int[] { 3161, 3162, 9351, 68656 }));
        COUNTRIES.put(30, new CountryStructure("South Korea", 30, new int[] { 3140, 3141, 3145, 98132, 117416 }));
        COUNTRIES.put(36, new CountryStructure("Spain", 36, new int[] { 3403, 3404, 3408, 3424, 5514, 14319, 38037 }));
        COUNTRIES.put(152, new CountryStructure("Sri Lanka", 152, new int[] { 258052, 258053, 258057, 258349 }));
        COUNTRIES.put(113, new CountryStructure("Suriname", 113, new int[] { 98835, 98836, 98840 }));
        COUNTRIES.put(1, new CountryStructure("Sweden", 1, new int[] { 1, 2, 6, 22, 86, 745 }));
        COUNTRIES.put(46, new CountryStructure("Switzerland", 46, new int[] { 4206, 8694, 8698, 11620, 16367, 20553, 30045 }));
        COUNTRIES.put(140, new CountryStructure("Syria", 140, new int[] { 238748, 238753, 238773, 253743 }));
        COUNTRIES.put(142, new CountryStructure("Tanzania", 142, new int[] { 238790, 238859, 239135 }));
        COUNTRIES.put(31, new CountryStructure("Thailand", 31, new int[] { 3119, 3120, 3124, 3790, 16964 }));
        COUNTRIES.put(110, new CountryStructure("Trinidad & Tobago", 110, new int[] { 98772, 98773, 98777 }));
        COUNTRIES.put(80, new CountryStructure("Tunisia", 80, new int[] { 53781, 53782, 53786, 238491, 253807 }));
        COUNTRIES.put(32, new CountryStructure("Turkey", 32, new int[] { 3098, 3099, 3103, 4362, 48917, 81088 }));
        COUNTRIES.put(143, new CountryStructure("Uganda", 143, new int[] { 245936, 245941, 245961 }));
        COUNTRIES.put(68, new CountryStructure("Ukraine", 68, new int[] { 33138, 33139, 33143, 65520, 122792 }));
        COUNTRIES.put(83, new CountryStructure("United Arab Emirates", 83, new int[] { 56880, 56885, 56905, 239407, 254063 }));
        COUNTRIES.put(28, new CountryStructure("Uruguay", 28, new int[] { 3013, 3014, 3018, 33159, 33815 }));
        COUNTRIES.put(8, new CountryStructure("USA", 8, new int[] { 597, 598, 602, 618, 8374, 27337 }));
        COUNTRIES.put(145, new CountryStructure("Uzbekistan", 145, new int[] { 252316, 252337, 252341 }));
        COUNTRIES.put(61, new CountryStructure("Wales", 61, new int[] { 16623, 16624, 16628, 21833, 203206 }));
        COUNTRIES.put(29, new CountryStructure("Venezuela", 29, new int[] { 3008, 3009, 13476, 33607, 56921 }));
        COUNTRIES.put(70, new CountryStructure("Vietnam", 70, new int[] { 28425, 28426, 28430, 76352, 249369 }));
        COUNTRIES.put(133, new CountryStructure("Yemen", 133, new int[] { 225688, 225693, 225697, 252911 }));

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
