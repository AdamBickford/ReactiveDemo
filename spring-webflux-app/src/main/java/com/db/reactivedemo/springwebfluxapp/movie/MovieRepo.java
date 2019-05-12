package com.db.reactivedemo.springwebfluxapp.movie;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class MovieRepo {

    private Flux<Movie> all;
    private Map<String, List<Movie>> movieByActor;
    private Map<String, List<Movie>> favorites = new HashMap<>();

    public Flux<Movie> all() {
        return all;
    }

    @PostConstruct
    public void init() {
        populate();
        Map<String, Movie> byTitle = all().collectMap(Movie::getTitle).block();
        favorites.put("sdoo", Arrays.asList(byTitle.get("Terminator"), byTitle.get("Rocky")));
        favorites.put("srogers", Arrays.asList(byTitle.get("Green Mile"), byTitle.get("Speed")));
        favorites.put("fjones", Arrays.asList(byTitle.get("Fight Club"), byTitle.get("Speed")));
        favorites.put("dblake", Arrays.asList(byTitle.get("The Expendables"), byTitle.get("Predator")));
    }

    private void populate() {
        populateAll();
        populateByActor();
    }

    private void populateByActor() {
        all.flatMap(it -> Flux.fromIterable(it.getActors()))
            .distinct()
            .flatMap(actor -> all.filter(movie -> movie.getActors().contains(actor))
                .collectList()
                .map(movies -> Tuples.of(actor, movies))
            )
            .collectMap(Tuple2::getT1, Tuple2::getT2)
            .subscribe(it -> this.movieByActor = it);

    }

    private void populateAll() {
        all = Flux.just(
            new Movie("The Terminator",
                Arrays.asList("Arnold Schwarzenegger",
                    "Linda Hamilton",
                    "Paul Winfield"
                )),
            new Movie("Predator",
                Arrays.asList("Arnold Schwarzenegger",
                    "Carl Weathers",
                    "Jesse Ventura"
                )),
            new Movie("Rocky",
                Arrays.asList("Sylvester Stallone",
                    "Carl Weathers",
                    "Talia Shire"
                )),
            new Movie("The Expendables",
                Arrays.asList("Arnold Schwarzenegger",
                    "Sylvester Stallone",
                    "Chuck Norris"
                )),
            new Movie("Men In Black",
                Arrays.asList("Will Smith",
                    "Tommy Lee Jones",
                    "Linda Fiorentino"
                )),
            new Movie("Speed",
                Arrays.asList("Keanu Reeves",
                    "Sandra Bullock",
                    "Dennis Hopper"
                )),
            new Movie("The Green Mile",
                Arrays.asList("Tom Hanks",
                    "David Morse",
                    "Bonnie Hunt"
                )),
            new Movie("Saving Private Ryan",
                Arrays.asList("Tom Hanks",
                    "Edward Burns",
                    "Matt Damon"
                )),
            new Movie("Oceans 11",
                Arrays.asList("Matt Damon",
                    "George Clooney",
                    "Brad Pitt"
                )),
            new Movie("Fight Club",
                Arrays.asList("Helena Bonham Carter",
                    "Edward Norton",
                    "Brad Pitt"
                )),
            new Movie("Seven",
                Arrays.asList("Morgan Freeman",
                    "Gwyneth Paltrow",
                    "Brad Pitt"
                ))
        );
    }

    public List<Movie> byUser(String userName) {
        return favorites.getOrDefault(userName, Collections.emptyList());
    }
}
