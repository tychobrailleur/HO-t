package module.matches;

import static core.util.Helper.getTranslation;

public enum MatchLocation {
    ALL,
    HOME,
    AWAY,
    NEUTRAL;

    public static String getText(MatchLocation matchLocation) {
        return switch (matchLocation) {
            case ALL -> getTranslation("ls.module.lineup.matchlocation.all");
            case HOME -> getTranslation("ls.module.lineup.matchlocation.home");
            case AWAY -> getTranslation("ls.module.lineup.matchlocation.away");
            case NEUTRAL -> getTranslation("ls.module.lineup.matchlocation.neutral");
        };
    }
}
