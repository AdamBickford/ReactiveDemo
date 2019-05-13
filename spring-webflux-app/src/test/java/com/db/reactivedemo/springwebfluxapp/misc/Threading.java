package com.db.reactivedemo.springwebfluxapp.misc;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

public class Threading {


    @Test
    public void workSync() {
        List<Integer> block = Flux.range(0, 100)
            .doOnNext(it -> log("Adding "))
            .map(i -> i + 10)
            .doOnNext(it -> log("Multiplying "))
            .map(i -> i * 2)
            .collectList()
            .block();
    }

    @Test
    public void workAsync() {
        List<Integer> numbers = Flux.range(0, 100)
            .parallel()
            .runOn(Schedulers.newParallel("Addition Thread"))
                .doOnNext(it -> log("Adding "))
                .map(i -> i + 10)
            .runOn(Schedulers.newParallel("Multiplication Thread"))
                .doOnNext(it -> log("Multiplying "))
                .map(i -> i * 2)
            .sequential()
            .collectList()
            .block();
    }


    public static void log(String message) {
        System.out.printf("thread %s -- %s\n", Thread.currentThread().getName(),message);
    }
}
