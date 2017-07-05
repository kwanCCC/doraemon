package org.doraemon.treasures.research;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;

public class ForkJoinPool_WorkQueue_Memory_Leaks {
    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool(5);
        ThreadLocalRandom current = ThreadLocalRandom.current();
        while (true) {
            Worker worker = new Worker(current.nextLong());
            pool.submit(worker);
        }
    }

    static class Worker extends RecursiveTask<Long> {

        private final long i;

        Worker(long i) {this.i = i;}

        @Override
        protected Long compute() {
            return i + 1;
        }
    }
}
