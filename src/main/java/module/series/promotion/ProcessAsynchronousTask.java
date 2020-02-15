package module.series.promotion;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ProcessAsynchronousTask<T> {

    @FunctionalInterface
    interface ProcessTask<T> {
        void execute(T val);
    }

    private final static int NUM_THREADS = 5;
    private final ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

    final Queue<T> queue = new LinkedBlockingQueue<>();

    public void addToQueue(T entry) {
        queue.add(entry);
    }

    public void execute(ProcessTask task) {
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                System.out.println("Executing service...");
                while (!queue.isEmpty()) {
                    T val = queue.poll();
                    task.execute(val);
                }

                executorService.shutdown();
            });
        }

        try {
            executorService.awaitTermination(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
