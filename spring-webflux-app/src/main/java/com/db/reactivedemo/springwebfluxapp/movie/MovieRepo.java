package com.db.reactivedemo.springwebfluxapp.movie;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class MovieRepo {

    private Flux<Movie> all;
    private Map<String, List<Movie>> movieByActor;
    private List<Favorites> favorites = new ArrayList<>();
    private List<History> histories = new ArrayList<>();

    public Flux<Movie> all() {
        return all;
    }

    public List<Favorites> getFavorites() {
        return favorites;
    }

    public List<History> getHistories() {
        return histories;
    }

    @PostConstruct
    public void init() {
        populate();
        Map<String, Movie> byTitle = all().collectMap(Movie::getTitle).block();

        favorites.add(new Favorites("sdoo", Arrays.asList(byTitle.get("The Terminator"))));
        histories.add(new History("sdoo", Arrays.asList(byTitle.get("The Terminator"), byTitle.get("Rocky"), byTitle.get("The Expendables"))));

        favorites.add(new Favorites("srogers", Arrays.asList(byTitle.get("The Green Mile"), byTitle.get("Speed"))));
        histories.add(new History("srogers", Arrays.asList(byTitle.get("The Green Mile"), byTitle.get("Speed"))));

        favorites.add(new Favorites("fjones", Arrays.asList(byTitle.get("Fight Club"), byTitle.get("Speed"))));
        histories.add(new History("fjones", Arrays.asList(byTitle.get("Fight Club"), byTitle.get("Seven"))));

        histories.add(new History("dblake", Arrays.asList(byTitle.get("Oceans 11"), byTitle.get("Predator"))));
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
            new Movie("Men In Black",
                Arrays.asList("Will Smith",
                    "Tommy Lee Jones",
                    "Linda Fiorentino"
                )),
            new Movie("Oceans 11",
                Arrays.asList("Matt Damon",
                    "George Clooney",
                    "Brad Pitt"
                )),
            new Movie("Saving Private Ryan",
                Arrays.asList("Tom Hanks",
                    "Edward Burns",
                    "Matt Damon"
                )),
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

    public List<Movie> getMoviesByActor(String actor) {
        return movieByActor.getOrDefault(actor, Collections.emptyList());
    }

    public Map<String, List<Movie>> getMoviesByActor() {
        return movieByActor;
    }

    public Mono<Favorites> userFavorites(String userName) {
        return Flux.fromIterable(favorites)
            .filter(it -> it.getUserName().equals(userName))
            .defaultIfEmpty(new Favorites(userName, Collections.emptyList()))
            .single();
    }
}
