package core.training;

import core.HO;
import core.model.HOModelManager;
import core.model.TranslationFacility;
import core.model.Translator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TrainingManagerTest {

    public static void main(String[] args) {

        HO.setPortable_version(true);
        HOModelManager.instance().loadLatestHoModel();
        TranslationFacility.setLanguage(Translator.LANGUAGE_DEFAULT);

        Instant endDate = Instant.now();
        Instant startDate = endDate.minus(7, ChronoUnit.DAYS);
    }


}
