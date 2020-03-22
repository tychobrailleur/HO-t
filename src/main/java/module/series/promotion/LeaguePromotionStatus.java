package module.series.promotion;

import java.util.HashMap;
import java.util.Map;

public enum LeaguePromotionStatus {

    DIRECT_DEMOTION,
    DEMOTION_MATCH_BARRAGE,
    NO_CHANGE,
    PROMOTION_MATCH_BARRAGE,
    DIRECT_PROMOTION,
    UNKNOWN;

    /*
        class PM(Enum):
    Undefined = -1
    DD = 0
    MD = 1
    S = 2
    MP = 3
    DP = 4

    DD = direct demotion
    MD = Demotion Match barrage
    S = no change
    MP = Promotion Match barrage
    DP = Direct Promotion

     */
    public static LeaguePromotionStatus codeToStatus(String code) {
        Map<String, LeaguePromotionStatus> mapping = new HashMap<>();
        mapping.put("DD", DIRECT_DEMOTION);
        mapping.put("MD", DEMOTION_MATCH_BARRAGE);
        mapping.put("S", NO_CHANGE);
        mapping.put("MP", PROMOTION_MATCH_BARRAGE);
        mapping.put("DP", DIRECT_DEMOTION);

        return mapping.getOrDefault(code, UNKNOWN);
    }
}
