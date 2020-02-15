package module.series.promotion;

import java.util.ArrayList;
import java.util.List;

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
    List<TeamRank> ranks = new ArrayList<>();
}
