package tool.dbcleanup;

import core.model.enums.MatchType;
import java.util.List;

public record CleanupDetails(List<MatchType>ownTeamMatchTypes,List<MatchType>otherTeamMatchTypes,int ownTeamWeeks,int otherTeamWeeks){}
