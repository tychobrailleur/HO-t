package module.series.promotion;

import java.util.function.Function;

public interface DataSubmitter {

    void getLeagueStatus(int leagueId, Function<String, Void> callback);
    void submitData(String json);
}
