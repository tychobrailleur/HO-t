package module.series.promotion;

import java.util.ArrayList;
import java.util.List;

public class CountryTeamInfo {

    static class TeamRank {

        public TeamRank(int teamId, long rank) {
            this.teamId = teamId;
            this.rank = rank;
        }

        int teamId;
        long rank;

        public int getTeamId() {
            return teamId;
        }

        public long getRank() {
            return rank;
        }

        public void setRank(long rank) {
            this.rank = rank;
        }
    }

    int countryId;
    List<TeamRank> ranks = new ArrayList<>();
}
