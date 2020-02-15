package module.series.promotion;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean used to generate the JSON payload submitted to the HO server.
 */
public class CountryTeamInfo {

    static class TeamRank {

        public TeamRank(int teamId, long calculatedRank) {
            this.teamId = teamId;
            this.calculatedRank = calculatedRank;
        }

        int teamId;
        long calculatedRank;

        public int getTeamId() {
            return teamId;
        }

        public long getCalculatedRank() {
            return calculatedRank;
        }

        public void setCalculatedRank(long calculatedRank) {
            this.calculatedRank = calculatedRank;
        }
    }

    int leagueId;
    int season;
    String username;
    List<TeamRank> calculatedRank = new ArrayList<>();
}
