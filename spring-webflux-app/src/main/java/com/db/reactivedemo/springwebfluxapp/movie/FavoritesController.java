package com.db.reactivedemo.springwebfluxapp.movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoritesController {

    private final MovieRepo movieRepo;

    @Autowired
    public FavoritesController(MovieRepo movieRepo) {
        this.movieRepo = movieRepo;
    }


    @GetMapping("/{userName}")
    public Mono<List<Movie>> forUser(@PathVariable String userName) {
        return Mono.justOrEmpty(movieRepo.byUser(userName));
    }

}
