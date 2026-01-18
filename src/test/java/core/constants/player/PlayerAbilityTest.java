package core.constants.player;

import core.HOModelBuilder;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.Translator;
import core.util.Helper;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerAbilityTest {

    @BeforeEach
    void setup() {
        HOVerwaltung hoAdmin = HOVerwaltung.instance();
        HOModel hoModel = new HOModelBuilder()
                .hrfId(42)
                .build();
        hoAdmin.setModel(hoModel);
        TranslationFacility.setTranslator(Translator.load(Translator.LANGUAGE_NO_TRANSLATION));
    }

    @Test
    void getValue4Sublevel() {
        Map<Integer, Double> expectedValues = Map.of(0, 0.0, 1, 0.25, 2, 0.5, 3, 0.75);
        expectedValues.forEach((key, value) -> Assertions.assertEquals(value, PlayerAbility.getValue4Sublevel(key)));
        Assertions.assertEquals(0.0, PlayerAbility.getValue4Sublevel(42));
    }

    @Test
    void getNameForSkill() {
        Assertions.assertEquals(
                String.format(
                        "!ls.player.skill.value.outstanding! (!verylow!) (%s)",
                        Helper.getNumberFormat(1).format(Helper.round(10.0, 1))),
                PlayerAbility.getNameForSkill(10.0, true, true, 1));
        Assertions.assertEquals(
                "!ls.player.skill.value.outstanding! (!verylow!)",
                PlayerAbility.getNameForSkill(10.0, false, true, 1));
        Assertions.assertEquals(
                "!ls.player.skill.value.outstanding!",
                PlayerAbility.getNameForSkill(10.0, false, false, 1));
        Assertions.assertEquals(
                String.format(
                        "!ls.player.skill.value.outstanding! (%s)",
                        Helper.getNumberFormat(1).format(Helper.round(10.0, 1))),
                PlayerAbility.getNameForSkill(10.0, true, false, 1));
        Assertions.assertEquals(
                String.format(
                        "!ls.player.skill.value.outstanding! (%s)",
                        Helper.getNumberFormat(2).format(Helper.round(10.0, 2))),
                PlayerAbility.getNameForSkill(10.0, true, false, 2));
        Assertions.assertEquals(
                "!ls.player.skill.value.divine!(+22) (!verylow!)",
                PlayerAbility.getNameForSkill(42.0, false, true, 1));
    }

    @Test
    void getName4Sublevel() {
        Assertions.assertEquals(
                String.format(
                        "!ls.player.skill.value.outstanding! (!low!) (%s)",
                        Helper.getNumberFormat(1).format(Helper.round(10.2, 1))),
                PlayerAbility.getNameForSkill(10.3, true, true, 1));
        Assertions.assertEquals(
                String.format(
                        "!ls.player.skill.value.outstanding! (!high!) (%s)",
                        Helper.getNumberFormat(1).format(Helper.round(10.5, 1))

                ),
                PlayerAbility.getNameForSkill(10.5, true, true, 1));
        Assertions.assertEquals(
                String.format(
                        "!ls.player.skill.value.outstanding! (!veryhigh!) (%s)",
                        Helper.getNumberFormat(1).format(Helper.round(10.8, 1))),
                PlayerAbility.getNameForSkill(10.8, true, true, 1));
    }
}
