package module.youth;

import core.constants.player.PlayerSkill;
import core.model.player.Specialty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.Properties;

class YouthPlayerTests {
    @Test
    void test() {
        // Prepare model
        Properties properties = new Properties();
        properties.setProperty("age", "16");
        properties.setProperty("agedays", "84");
        properties.setProperty("canbepromotedin", "96");
        properties.setProperty("defenderskill", "4");
        properties.setProperty("defenderskillmax", "8");
        properties.setProperty("passingskillmax", "4");
        properties.setProperty("scorerskillmax", "5");
        YouthPlayer youthPlayer = new YouthPlayer(properties);
        Assertions.assertEquals(2158, youthPlayer.calculateRateMyAcademyScore());

        youthPlayer.setAgeDays(youthPlayer.getAgeDays() + 1);
        Assertions.assertEquals(2148, youthPlayer.calculateRateMyAcademyScore());

        youthPlayer.setCanBePromotedIn(youthPlayer.getCanBePromotedIn() + 1);
        Assertions.assertEquals(2138, youthPlayer.calculateRateMyAcademyScore());

        youthPlayer.setSpecialty(Specialty.Head);
        Assertions.assertEquals(2238, youthPlayer.calculateRateMyAcademyScore());

        youthPlayer.setMax(PlayerSkill.PLAYMAKING, 8);
        Assertions.assertEquals(2830, youthPlayer.calculateRateMyAcademyScore());

        youthPlayer.setSpecialty(Specialty.Regainer);
        Assertions.assertEquals(2730, youthPlayer.calculateRateMyAcademyScore());

        youthPlayer.setSpecialty(Specialty.NoSpecialty);
        Assertions.assertEquals(2630, youthPlayer.calculateRateMyAcademyScore());

        properties.setProperty("scoutcomment0text", "");
        properties.setProperty("scoutcomment0type", "6"); // overallskill type
        properties.setProperty("scoutcomment0skilltype", "7"); // solid value (never seen that in real hattrick;-)
        properties.setProperty("playmakerskillmax", "8");

        youthPlayer = new YouthPlayer(properties);
        Assertions.assertEquals(2928, youthPlayer.calculateRateMyAcademyScore());
    }

    @BeforeEach
    void cleanUp() {
        File file = new File("null");
        if (file.exists()) {
            deleteRecursively(file);
        }
    }

    private void deleteRecursively(File file) {
        if (file.isDirectory()) {
            for (File c : file.listFiles()) {
                deleteRecursively(c);
            }
        }
        file.delete();
    }
}
