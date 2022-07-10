package adam.bickford.reactivedemo.springwebfluxapp.movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping
public class MovieController {

    private final MovieRepo movieRepo;

    @Autowired
    public MovieController(MovieRepo movieRepo) {
        this.movieRepo = movieRepo;
    }

    @GetMapping("/all")
    public Flux<Movie> all() {
        return movieRepo.getAll();
    }

    @GetMapping("/favorites/{userName}")
    public Mono<Favorites> favorites(@PathVariable String userName) {
        return movieRepo.userFavorites(userName).doOnNext(it -> {
            if (ThreadLocalRandom.current().nextInt(10) < 8) {
                throw new RuntimeException("Unreliable Service");
            }
        })
            ;
    }

    @GetMapping("/history/{userName}")
    public Mono<History> history(@PathVariable String userName) {
        return Flux.fromIterable(movieRepo.getHistories())
            .filter(history -> history.getUserName().equals(userName))
            .defaultIfEmpty(new History(userName, Collections.emptyList()))
            .single();
    }

    @GetMapping("/actor/{actor}")
    public Flux<Movie> byActor(@PathVariable String actor) {
        return byActor2(actor)
            .delayElements(Duration.ofSeconds(
                ThreadLocalRandom.current().nextBoolean() ? 0 : 2)
            )
            ;
    }

    @GetMapping("/actor2/{actor}")
    public Flux<Movie> byActor2(@PathVariable String actor) {
        return Flux.fromIterable(movieRepo.getMoviesByActor(actor));
    }
}
