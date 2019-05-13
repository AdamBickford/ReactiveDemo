package com.db.reactivedemo.springwebfluxapp.misc;

import com.db.reactivedemo.springwebfluxapp.user.User;
import com.db.reactivedemo.springwebfluxapp.user.UserRepo;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactiveOperators {

    private UserRepo userRepo = new UserRepo();
    private Flux<String> abc = Flux.just("a", "b", "c");
    private Flux<String> xyz = Flux.just("x", "y", "z");

    @Test
    public void operators() {
        Flux<User> users = userRepo.getAllUsers();


    }

}
