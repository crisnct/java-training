package com.example.training;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrencyImprovements {

    private static final int THREADS = 5;

    private static final ConcurrentHashMap<String, Integer> scores = new ConcurrentHashMap<String, Integer>();
    private static final CopyOnWriteArrayList<String> log = new CopyOnWriteArrayList<String>();

    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final ReentrantLock lock = new ReentrantLock();
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private static final Semaphore semaphore = new Semaphore(1, false);   // limit concurrency
    private static final CountDownLatch latch = new CountDownLatch(THREADS);

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                System.out.println("Scheduled log cleanup: " + log.size() + " entries");
            }
        }, 1, 11, TimeUnit.SECONDS);

        for (int i = 0; i < THREADS; i++) {
            final int id = i;
            pool.submit(new Callable<String>() {
                public String call() throws Exception {
                  System.out.println("Start task " + id);
                  semaphore.acquire();
                    try {
                        incrementScore("worker-" + id);
                        Thread.sleep(1000); // simulate work
                        latch.countDown();
                        return "Task " + id + " done.";
                    } finally {
                        semaphore.release();
                        System.out.println("End task " + id);
                    }
                }
            });
        }

        latch.await();                 // wait until all tasks finish
        pool.shutdown();
        scheduler.shutdown();

        System.out.println("Final scores: " + scores);
        System.out.println("Total increments: " + counter.get());
    }

    private static void incrementScore(String name) {
        lock.lock();
        try {
            int newVal = counter.incrementAndGet();
            scores.put(name, newVal);
            log.add("Updated by " + name);
        } finally {
            lock.unlock();
        }
    }
}
