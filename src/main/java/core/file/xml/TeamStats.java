package core.file.xml;

public class TeamStats {

    private int teamId;
    private String teamName;

    private String leagueName;
    private int leagueRank;

    private int position;
    private int points;
    private int goalsFor;
    private int goalsAgainst;

    private int observedRank;

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getLeagueRank() {
        return leagueRank;
    }

    public void setLeagueRank(int leagueRank) {
        this.leagueRank = leagueRank;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public void setGoalsFor(int goalsFor) {
        this.goalsFor = goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public int getGoalsDiff() {
        return getGoalsFor() - getGoalsAgainst();
    }

    public int getObservedRank() {
        return observedRank;
    }

    public void setObservedRank(int observedRank) {
        this.observedRank = observedRank;
    }

    public int rankingScore() {
        return (10-leagueRank) * 100_000_000 + (8-position) * 10_000_000 + points * 100_000 + getGoalsDiff() * 1_000 + goalsFor;
    }

    public String toString() {
        return "TeamStats[ " + teamName + " rank: " + getObservedRank() + " score: " + rankingScore() + " ]";
    }
}
