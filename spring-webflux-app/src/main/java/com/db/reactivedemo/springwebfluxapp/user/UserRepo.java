package com.db.reactivedemo.springwebfluxapp.user;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class UserRepo {
    private Flux<User> all;
    private Map<String, User> byUserName;

    public UserRepo() {
        populate();
    }

    public Flux<User> getAllUsers() {
        return all;
    }

    private void populate() {
        all = Flux.just(
            new User("sdoo", "Scooby", "Doo"),
            new User("fjones", "Fred", "Jones"),
            new User("srogers", "Shaggy", "Rogers"),
            new User("dblake", "Daphne", "Blake"),
            new User("vdinkley", "Velma", "Dinkley")
        );
        byUserName = all.collectMap(User::getUserName)
            .block();
    }

    public Mono<User> byUserName(String userName) {
        return Mono.just(byUserName.get(userName));
    }
}
