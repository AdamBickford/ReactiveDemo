package com.db.reactivedemo.springwebfluxapp.misc;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ContextAware {

    public static void main(String[] args) {
        new ContextAware().collect();
    }

    @Test
    public void collect() {
        Set<Integer> collect = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            collect.add(i);
        }
        Assert.assertEquals(1000, collect.size());
    }

    @Test
    public void collectParallel() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(8);

        Set<Integer> collect = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            final int tmp = i;
            executorService.submit(() -> {
                doWork();
                collect.add(tmp);
            });

        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        Assert.assertEquals(1000, collect.size());
    }

    @Test
    public void collectParallelFunctional() {
        Set<Integer> collect = IntStream.range(0, 1000)
            .boxed()
            .parallel()
            .peek(it -> doWork())
            .collect(Collectors.toSet());
        Assert.assertEquals(1000, collect.size());
    }

    public void doWork() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(10, 20));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
