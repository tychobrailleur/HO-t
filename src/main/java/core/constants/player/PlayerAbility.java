package core.constants.player;

import core.datatype.CBItem;
import core.model.TranslationFacility;
import core.model.UserParameter;
import core.util.Helper;

public final class PlayerAbility {

    private static final String[] LANGUAGE_KEYS = {
            "ls.player.skill.value.non-existent",
            "ls.player.skill.value.disastrous",
            "ls.player.skill.value.wretched",
            "ls.player.skill.value.poor",
            "ls.player.skill.value.weak",
            "ls.player.skill.value.inadequate",
            "ls.player.skill.value.passable",
            "ls.player.skill.value.solid",
            "ls.player.skill.value.excellent",
            "ls.player.skill.value.formidable",
            "ls.player.skill.value.outstanding",
            "ls.player.skill.value.brilliant",
            "ls.player.skill.value.magnificent",
            "ls.player.skill.value.worldclass",
            "ls.player.skill.value.supernatural",
            "ls.player.skill.value.titanic",
            "ls.player.skill.value.extra-terrestrial",
            "ls.player.skill.value.mythical",
            "ls.player.skill.value.magical",
            "ls.player.skill.value.utopian",
            "ls.player.skill.value.divine"
    };

    public static final int NON_EXISTENT = 0;
    public static final int DISASTROUS = 1;
    public static final int WRETCHED = 2;
    public static final int POOR = 3;
    public static final int WEAK = 4;
    public static final int INADEQUATE = 5;
    public static final int PASSABLE = 6;
    public static final int SOLID = 7;
    public static final int EXCELLENT = 8;
    public static final int FORMIDABLE = 9;
    public static final int OUTSTANDING = 10;
    public static final int BRILLIANT = 11;
    public static final int MAGNIFICENT = 12;
    public static final int WORLD_CLASS = 13;
    public static final int SUPERNATURAL = 14;
    public static final int TITANIC = 15;
    public static final int EXTRA_TERRESTRIAL = 16;
    public static final int MYTHICAL = 17;
    public static final int MAGICAL = 18;
    public static final int UTOPIAN = 19;
    public static final int DIVINE = 20;

    public static final CBItem[] ITEMS = {
            new CBItem(getNameForSkill((double) NON_EXISTENT), NON_EXISTENT),
            new CBItem(getNameForSkill((double) DISASTROUS), DISASTROUS),
            new CBItem(getNameForSkill((double) WRETCHED), WRETCHED),
            new CBItem(getNameForSkill((double) POOR), POOR),
            new CBItem(getNameForSkill((double) WEAK), WEAK),
            new CBItem(getNameForSkill((double) INADEQUATE), INADEQUATE),
            new CBItem(getNameForSkill((double) PASSABLE), PASSABLE),
            new CBItem(getNameForSkill((double) SOLID), SOLID),
            new CBItem(getNameForSkill((double) EXCELLENT), EXCELLENT),
            new CBItem(getNameForSkill((double) FORMIDABLE), FORMIDABLE),
            new CBItem(getNameForSkill((double) OUTSTANDING), OUTSTANDING),
            new CBItem(getNameForSkill((double) BRILLIANT), BRILLIANT),
            new CBItem(getNameForSkill((double) MAGNIFICENT), MAGNIFICENT),
            new CBItem(getNameForSkill((double) WORLD_CLASS), WORLD_CLASS),
            new CBItem(getNameForSkill((double) SUPERNATURAL), SUPERNATURAL),
            new CBItem(getNameForSkill((double) TITANIC), TITANIC),
            new CBItem(getNameForSkill((double) EXTRA_TERRESTRIAL), EXTRA_TERRESTRIAL),
            new CBItem(getNameForSkill((double) MYTHICAL), MYTHICAL),
            new CBItem(getNameForSkill((double) MAGICAL), MAGICAL),
            new CBItem(getNameForSkill((double) UTOPIAN), UTOPIAN),
            new CBItem(getNameForSkill((double) DIVINE), DIVINE)
    };

    private PlayerAbility() {
        // Private constructor to prevent instantiation
    }

    public static String toString(int ability) {
        if (ability >= NON_EXISTENT && ability <= DIVINE) {
            return TranslationFacility.tr(LANGUAGE_KEYS[ability]);
        } else {
            String value = TranslationFacility.tr(ability > DIVINE ? LANGUAGE_KEYS[DIVINE] : "Unbestimmt");
            if (ability > 20) {
                value += "(+" + (ability - 20) + ")";
            }
            return value;
        }
    }

    /**
     * get string representation of rating values
     *
     * @param ratingValue double [0..20]
     * @param showNumbers true for numerical representation
     * @param isMatch     true shows' sub-level representations
     * @param nbDecimal   nbDecimalDisplayed
     * @return String
     */
    public static String getNameForSkill(double ratingValue, boolean showNumbers, boolean isMatch, int nbDecimal) {
        int tmpRatingValue = (int) ratingValue;
        int sublevel = 0;
        if (isMatch) {
            sublevel = (int) (ratingValue * 4) % 4;
        }

        StringBuilder rating = new StringBuilder(toString(tmpRatingValue));

        if (isMatch) {
            rating.append(getName4Sublevel(sublevel));
        }

        if (showNumbers) {
            rating.append(" (");
            if (isMatch) {
                rating.append(Helper.getNumberFormat(nbDecimal)
                        .format(Helper.round(tmpRatingValue + getValue4Sublevel(sublevel), 2)));
            } else {
                rating.append(Helper.getNumberFormat(nbDecimal)
                        .format(Helper.round(ratingValue, nbDecimal)));
            }
            rating.append(")");
        }
        return rating.toString();
    }

    /**
     * get string representation of rating values
     *
     * @param ratingValue double [0..20]
     * @param showNumbers true for numerical representation
     * @param isMatch     true shows' sub-level representations
     * @return String
     */
    public static String getNameForSkill(double ratingValue, boolean showNumbers, boolean isMatch) {
        return getNameForSkill(ratingValue, showNumbers, isMatch, UserParameter.instance().nbDecimals);
    }

    public static String getNameForSkill(boolean isMatch, double ratingValue) {
        return getNameForSkill(ratingValue, UserParameter.instance().zahlenFuerSkill, isMatch);
    }

    public static String getNameForSkill(double ratingValue, boolean zahlen) {
        return getNameForSkill(ratingValue, zahlen, false);
    }

    public static String getNameForSkill(double bewertung) {
        return getNameForSkill(bewertung, UserParameter.instance().zahlenFuerSkill);
    }

    public static double getValue4Sublevel(int sub) {
        return switch (sub) {
            case 1 -> 0.25;
            case 2 -> 0.5;
            case 3 -> 0.75;
            default -> 0.0;
        };
    }

    private static String getName4Sublevel(int sub) {
        return switch (sub) {
            case 0 -> " (" + TranslationFacility.tr("verylow") + ")";
            case 1 -> " (" + TranslationFacility.tr("low") + ")";
            case 2 -> " (" + TranslationFacility.tr("high") + ")";
            case 3 -> " (" + TranslationFacility.tr("veryhigh") + ")";
            default -> "";
        };
    }
}
