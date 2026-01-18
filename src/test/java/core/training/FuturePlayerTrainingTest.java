package core.training;

import core.util.HODateTime;
import core.util.HODateTime.HTWeek;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FuturePlayerTrainingTest {

    @Test
    void testFuturePlayerTrainingTestCut() {
        FuturePlayerTraining futurePlayerTraining = new FuturePlayerTraining(
                4711,
                FuturePlayerTraining.Priority.NO_TRAINING,
                HODateTime.fromHTWeek(new HTWeek(86, 1)),
                null);

        List<FuturePlayerTraining> remaining = futurePlayerTraining.cut(HODateTime.fromHTWeek(new HTWeek(86, 4)),
                HODateTime.fromHTWeek(new HTWeek(86, 5)));
        Assertions.assertEquals(2, remaining.size());
        FuturePlayerTraining f = remaining.get(0);
        Assertions.assertEquals(4711, f.getPlayerId());
        Assertions.assertEquals(FuturePlayerTraining.Priority.NO_TRAINING, f.getPriority());
        Assertions.assertEquals(86, f.getFromSeason());
        Assertions.assertEquals(1, f.getFromWeek());
        Assertions.assertEquals(86, f.getToSeason());
        Assertions.assertEquals(3, f.getToWeek());

        f = remaining.get(1);
        Assertions.assertEquals(4711, f.getPlayerId());
        Assertions.assertEquals(FuturePlayerTraining.Priority.NO_TRAINING, f.getPriority());
        Assertions.assertEquals(86, f.getFromSeason());
        Assertions.assertEquals(5, f.getFromWeek());
        Assertions.assertNull(f.getToSeason());
        Assertions.assertNull(f.getToWeek());

        remaining = futurePlayerTraining.cut(HODateTime.fromHTWeek(new HTWeek(86, 4)), null);
        Assertions.assertEquals(1, remaining.size());
        f = remaining.get(0);
        Assertions.assertEquals(4711, f.getPlayerId());
        Assertions.assertEquals(FuturePlayerTraining.Priority.NO_TRAINING, f.getPriority());
        Assertions.assertEquals(86, f.getFromSeason());
        Assertions.assertEquals(1, f.getFromWeek());
        Assertions.assertEquals(86, f.getToSeason());
        Assertions.assertEquals(3, f.getToWeek());

        remaining = futurePlayerTraining.cut(HODateTime.fromHTWeek(new HTWeek(85, 4)),
                HODateTime.fromHTWeek(new HTWeek(86, 4)));
        Assertions.assertEquals(1, remaining.size());
        f = remaining.get(0);
        Assertions.assertEquals(4711, f.getPlayerId());
        Assertions.assertEquals(FuturePlayerTraining.Priority.NO_TRAINING, f.getPriority());
        Assertions.assertEquals(86, f.getFromSeason());
        Assertions.assertEquals(4, f.getFromWeek());
        Assertions.assertNull(f.getToSeason());
        Assertions.assertNull(f.getToWeek());

        remaining = futurePlayerTraining.cut(HODateTime.fromHTWeek(new HTWeek(85, 4)), null);
        Assertions.assertEquals(0, remaining.size());

        futurePlayerTraining = new FuturePlayerTraining(
                4711,
                FuturePlayerTraining.Priority.NO_TRAINING,
                HODateTime.fromHTWeek(new HTWeek(86, 1)),
                HODateTime.fromHTWeek(new HTWeek(86, 3)));

        remaining = futurePlayerTraining.cut(HODateTime.fromHTWeek(new HTWeek(86, 5)),
                HODateTime.fromHTWeek(new HTWeek(86, 5)));
        Assertions.assertEquals(1, remaining.size());
        f = remaining.get(0);
        Assertions.assertEquals(4711, f.getPlayerId());
        Assertions.assertEquals(FuturePlayerTraining.Priority.NO_TRAINING, f.getPriority());
        Assertions.assertEquals(86, f.getFromSeason());
        Assertions.assertEquals(1, f.getFromWeek());
        Assertions.assertEquals(86, f.getToSeason());
        Assertions.assertEquals(3, f.getToWeek());

        remaining = futurePlayerTraining.cut(HODateTime.fromHTWeek(new HTWeek(86, 5)), null);
        Assertions.assertEquals(1, remaining.size());
        f = remaining.get(0);
        Assertions.assertEquals(4711, f.getPlayerId());
        Assertions.assertEquals(FuturePlayerTraining.Priority.NO_TRAINING, f.getPriority());
        Assertions.assertEquals(86, f.getFromSeason());
        Assertions.assertEquals(1, f.getFromWeek());
        Assertions.assertEquals(86, f.getToSeason());
        Assertions.assertEquals(3, f.getToWeek());

        remaining = futurePlayerTraining.cut(HODateTime.fromHTWeek(new HTWeek(86, 3)), null);
        Assertions.assertEquals(1, remaining.size());
        f = remaining.get(0);
        Assertions.assertEquals(4711, f.getPlayerId());
        Assertions.assertEquals(FuturePlayerTraining.Priority.NO_TRAINING, f.getPriority());
        Assertions.assertEquals(86, f.getFromSeason());
        Assertions.assertEquals(1, f.getFromWeek());
        Assertions.assertEquals(86, f.getToSeason());
        Assertions.assertEquals(2, f.getToWeek());

        remaining = futurePlayerTraining.cut(HODateTime.fromHTWeek(new HTWeek(85, 3)),
                HODateTime.fromHTWeek(new HTWeek(86, 2)));
        Assertions.assertEquals(1, remaining.size());
        f = remaining.get(0);
        Assertions.assertEquals(4711, f.getPlayerId());
        Assertions.assertEquals(FuturePlayerTraining.Priority.NO_TRAINING, f.getPriority());
        Assertions.assertEquals(86, f.getFromSeason());
        Assertions.assertEquals(2, f.getFromWeek());
        Assertions.assertEquals(86, f.getToSeason());
        Assertions.assertEquals(3, f.getToWeek());

        remaining = futurePlayerTraining.cut(HODateTime.fromHTWeek(new HTWeek(85, 3)),
                HODateTime.fromHTWeek(new HTWeek(86, 3)));
        Assertions.assertEquals(0, remaining.size());
    }
}
