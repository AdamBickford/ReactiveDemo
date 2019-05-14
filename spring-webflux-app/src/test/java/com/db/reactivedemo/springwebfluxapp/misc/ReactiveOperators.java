package com.db.reactivedemo.springwebfluxapp.misc;

import com.db.reactivedemo.springwebfluxapp.user.User;
import com.db.reactivedemo.springwebfluxapp.user.UserRepo;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

public class ReactiveOperators {

    private UserRepo userRepo = new UserRepo();
    private Flux<String> abc = Flux.just("a", "b", "c");
//    private Flux<String> xyz = Flux.just("x", "y", "z");

    @Test
    public void operators() {
        Flux<String> xyz = Mono.fromCallable(ReactiveOperators::getXyz).flatMapMany(Flux::fromStream);


        abc
            .skip(3)
            .take(2)
            .concatWith(xyz)
            .collectList()
            .block();

        Flux<User> users = userRepo.getAllUsers();


    }

    private static Stream<String> getXyz() {
        System.out.println("Getting xyz data");
        return Stream.of("x", "y", "z");
    }

}
