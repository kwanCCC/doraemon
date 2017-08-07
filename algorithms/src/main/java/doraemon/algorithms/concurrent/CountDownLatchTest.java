package doraemon.algorithms.concurrent;

import lombok.AllArgsConstructor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class CountDownLatchTest {

    static AtomicInteger integer = new AtomicInteger(0);

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(3, r -> {
            Thread thread = new Thread(r);
            thread.setName("" + integer.getAndAdd(1));
            return thread;
        });
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Counter(countDownLatch));
            if (i == 9) {
                countDownLatch.countDown();
            }
        }
        executorService.shutdown();
    }

    @AllArgsConstructor
    public static class Counter implements Runnable {

        private final CountDownLatch latch;

        @Override
        public void run() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            System.out.println("finish");
        }
    }
}
