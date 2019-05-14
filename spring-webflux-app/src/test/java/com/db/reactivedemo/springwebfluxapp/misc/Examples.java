package com.db.reactivedemo.springwebfluxapp.misc;

import com.db.reactivedemo.springwebfluxapp.movie.Movie;
import com.db.reactivedemo.springwebfluxapp.movie.MovieRepo;
import com.db.reactivedemo.springwebfluxapp.user.User;
import com.db.reactivedemo.springwebfluxapp.user.UserRepo;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Examples {

    private List<User> users;
    private List<Movie> movies;

    public Examples() {
        users = new UserRepo().getAllUsers()
            .collectList()
            .block();

        movies = new MovieRepo().getAll()
            .collectList()
            .block();

    }

    @Test
    public void mapProcedural() {
        List<String> fullNames = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            fullNames.add(getFullName(user));
        }
        Assert.assertEquals(users.size(), fullNames.size());
    }

    @Test
    public void mapProceduralParallel() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(8);

        List<String> fullNames = new ArrayList<>();
        int times = 200;
        for (int i = 0; i < times; i++) {
            for (int j = 0; j < users.size(); j++) {
                User user = users.get(j);
                executorService.submit(() -> {
                    fullNames.add(getFullName(user));
                });
            }
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        Assert.assertEquals(users.size() * times, fullNames.size());
    }

    @Test
    public void mapFunctional() {
        List<String> fullNames = users.stream()
            .map(this::getFullName)
            .collect(Collectors.toList());
        Assert.assertEquals(users.size(), fullNames.size());
    }

    @Test
    public void mapFunctionalParallel() {
        int times = 200;
        List<String> fullNames = IntStream.range(0, times).boxed()
            .parallel()
            .flatMap(i -> users.stream())
            .map(this::getFullName)
            .collect(Collectors.toList());
        Assert.assertEquals(users.size() * times, fullNames.size());
    }


    private Set<String> composeProcedural() {
        Set<String> actors = new HashSet<>();
        for (Movie movie : movies) {
            for (String actor : movie.getActors()) {
                if (actor.contains("m")) {
                    if (actor.contains("a")) {
                        if (actors.size() < 3) {
                            actors.add(actor);
                        }
                    }
                }
            }
        }
        return actors;
    }

    private Set<String> composeFunctional() {
        return movies.stream()
            .flatMap(movie -> movie.getActors().stream())
            .distinct()
            .filter(actor -> actor.contains("m"))
            .filter(actor -> actor.contains("a"))
            .limit(3)
            .collect(Collectors.toSet());
    }

    @Test
    public void composeEqual() {
        Set<String> functional = composeFunctional();
        Set<String> procedural = composeProcedural();
        Assert.assertEquals(functional, procedural);
    }

    @Test
    public void lazy() {
        Supplier<List<String>> abc = () -> Arrays.asList("a", "b", "c");

        Supplier<List<String>> xyz = () -> {
            System.out.println("getting xyz");
            return Arrays.asList("x", "y", "z");
        };

        List<String> values = Stream.of(abc, xyz)
            .peek(listSupplier -> System.out.println("list supplier -> " + listSupplier))
            .map(Supplier::get)
            .peek(list -> System.out.println("\tlist -> " + list))
            .flatMap(List::stream)
            .peek(letter -> System.out.println("\t\tletter  -> " + letter))
//            .skip(4)
            .limit(2)
            .peek(letter -> System.out.println("\t\t\tfiltered letter  -> " + letter))
            .collect(Collectors.toList());
        System.out.println("----------------");
        System.out.println("values: " + values);
    }

    @Test
    public void lazyFlux() {
        Supplier<List<String>> abc = () -> Arrays.asList("a", "b", "c");

        Supplier<List<String>> xyz = () -> {
            System.out.println("getting xyz");
            return Arrays.asList("x", "y", "z");
        };

        List<String> block = Flux.just(abc, xyz)
            .doOnNext(listSupplier -> System.out.println("list supplier -> " + listSupplier))
            .map(Supplier::get)
            .doOnNext(list -> System.out.println("\tlist -> " + list))
            .flatMap(Flux::fromIterable)
            .doOnNext(letter -> System.out.println("\t\tletter  -> " + letter))
            .take(2)
            .doOnNext(letter -> System.out.println("\t\t\tfiltered letter  -> " + letter))
            .collectList()
            .block();

        System.out.println("----------------");
        System.out.println("values: " + block);
    }

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
            .runOn(Schedulers.newParallel("Addition Thread", 8))
            .doOnNext(it -> log("Adding "))
            .map(i -> i + 10)
            .runOn(Schedulers.newParallel("Multiplication Thread", 8))
            .doOnNext(it -> log("Multiplying "))
            .map(i -> i * 2)
            .sequential()
            .collectList()
            .block();
    }


    private static void log(String message) {
        System.out.printf("thread %s -- %s\n", Thread.currentThread().getName(), message);
    }

    private String getFullName(User user) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(10, 20));
            return String.format("%s %s", user.getFirstName(), user.getLastName());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
