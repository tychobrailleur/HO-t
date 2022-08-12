// %233313029:de.hattrickorganizer.model%
package core.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Team data, except players.
 */
public record Team(
        int formationExperience343,
        int formationExperience352,
        int formationExperience433,
        int formationExperience451,
        int formationExperience532,
        int formationExperience541,
        int formationExperience442,
        int formationExperience523,
        int formationExperience550,
        int formationExperience253,
        int confidence,    // Confidence
        int teamSpirit,        // Team spirit
        int subTeamSpirit,           // Sub Team spirit
        int trainingType,       // Training type
        int trainingsLevel,     // Training level
        int staminaTrainingPart // Stamina training level
) {
    public final static int DEFAULT_SUB_TEAM_SPIRIT = 2;

    // ~ Constructors
    // -------------------------------------------------------------------------------

    public Team(Properties properties) throws Exception {
        this(Integer.parseInt(properties.getProperty("exper343", "0")),
                Integer.parseInt(properties.getProperty("exper352", "0")),
                Integer.parseInt(properties.getProperty("exper433", "0")),
                Integer.parseInt(properties.getProperty("exper451", "0")),
                Integer.parseInt(properties.getProperty("exper532", "0")),
                Integer.parseInt(properties.getProperty("exper541", "0")),
                Integer.parseInt(properties.getProperty("exper442", "0")),
                Integer.parseInt(properties.getProperty("exper523", "0")),
                Integer.parseInt(properties.getProperty("exper550", "0")),
                Integer.parseInt(properties.getProperty("exper253", "0")),
                Integer.parseInt(properties.getProperty("sjalvfortroendevalue", "0")),
                Integer.parseInt(properties.getProperty("stamningvalue", "0")),
                DEFAULT_SUB_TEAM_SPIRIT,
                Integer.parseInt(properties.getProperty("trtypevalue", "-1")),
                Integer.parseInt(properties.getProperty("trlevel", "0")),
                Integer.parseInt(properties.getProperty("staminatrainingpart", "0")));
    }


    /**
     * Creates a new Team object.
     */
    public Team() {
        this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }


    /**
     * Creates a new Team object.
     */
    public Team(ResultSet rs) throws Exception {
        this(
                rs.getInt("iErfahrung343"),
                rs.getInt("iErfahrung352"),
                rs.getInt("iErfahrung433"),
                rs.getInt("iErfahrung451"),
                rs.getInt("iErfahrung532"),
                rs.getInt("iErfahrung541"),
                rs.getInt("iErfahrung442"),
                rs.getInt("iErfahrung523"),
                rs.getInt("iErfahrung550"),
                rs.getInt("iErfahrung253"),
                rs.getInt("iSelbstvertrauen"),
                rs.getInt("iStimmung"),
                DEFAULT_SUB_TEAM_SPIRIT,
                rs.getInt("TrainingsArt"),
                rs.getInt("TrainingsIntensitaet"),
                rs.getInt("StaminaTrainingPart")
        );
    }

    public Team setConfidence(int confidence0) {
        return new Team(
                formationExperience343,
                formationExperience352,
                formationExperience433,
                formationExperience451,
                formationExperience532,
                formationExperience541,
                formationExperience442,
                formationExperience523,
                formationExperience550,
                formationExperience253,
                confidence0,
                teamSpirit,
                subTeamSpirit,
                trainingType,
                trainingsLevel,
                staminaTrainingPart
        );
    }

    public Team setTeamSpirit(int teamSpirit0) {
        return new Team(
                formationExperience343,
                formationExperience352,
                formationExperience433,
                formationExperience451,
                formationExperience532,
                formationExperience541,
                formationExperience442,
                formationExperience523,
                formationExperience550,
                formationExperience253,
                confidence,
                teamSpirit0,
                subTeamSpirit,
                trainingType,
                trainingsLevel,
                staminaTrainingPart
        );
    }

    public Team setSubTeamSpirit(int subTeamSpirit0) {
        return new Team(
                formationExperience343,
                formationExperience352,
                formationExperience433,
                formationExperience451,
                formationExperience532,
                formationExperience541,
                formationExperience442,
                formationExperience523,
                formationExperience550,
                formationExperience253,
                confidence,
                teamSpirit,
                subTeamSpirit0,
                trainingType,
                trainingsLevel,
                staminaTrainingPart
        );
    }
}
