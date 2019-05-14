package com.db.reactivedemo.springwebfluxapp;

import com.db.reactivedemo.springwebfluxapp.movie.Favorites;
import com.db.reactivedemo.springwebfluxapp.movie.Movie;
import com.db.reactivedemo.springwebfluxapp.movie.MovieRepo;
import com.db.reactivedemo.springwebfluxapp.user.User;
import com.db.reactivedemo.springwebfluxapp.user.UserInfo;
import com.db.reactivedemo.springwebfluxapp.user.UserRepo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.List;
import java.util.Map;


@SpringBootTest
@RunWith(SpringRunner.class)
@TestConfiguration
public class ServiceTests {

    private Solution solution = new Solution();

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MovieRepo movieRepo;

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void resolveUserNamesBlocking() throws Exception {
        List<User> all = userRepo.getAllUsers().collectList().block();

        Flux<String> userNames = userRepo.getAllUsers()
            .map(User::getUserName);

        Flux<User> timeout = userNames
            .parallel()
            .runOn(Schedulers.elastic())
            .map(userName -> restTemplate.getForObject(String.format("http://localhost:8090/user/%s", userName), User.class))
            .sequential();

        StepVerifier.create(timeout.collectList()
            .timeout(Duration.ofSeconds(3)))
            .assertNext(it -> {
                Assert.assertTrue(all.containsAll(it));
                Assert.assertEquals(all.size(), it.size());
            })
            .verifyComplete()
        ;

    }

    @Test
    public void resolveUserNamesAsync() {
        List<User> all = userRepo.getAllUsers().collectList().block();

        Flux<User> result = Flux.fromIterable(all)
            .map(User::getUserName)
            .flatMap(it -> solution.getUserByName(it));

        StepVerifier.create(result.collectList()
            .timeout(Duration.ofSeconds(3))
        )
            .assertNext(it -> {
                Assert.assertTrue(all.containsAll(it));
                Assert.assertEquals(all.size(), it.size());
            })
            .verifyComplete();
    }

    @Test
    public void getFavorites() throws InterruptedException {
        List<Favorites> favorites = movieRepo.getFavorites();
        Flux<Favorites> userFavorites = Flux.fromIterable(favorites)
            .map(Favorites::getUserName)
            .flatMap(solution::getFavoritesForUser);

        StepVerifier.create(userFavorites.collectList())
            .assertNext(it -> {
                Assert.assertTrue(favorites.containsAll(it));
                Assert.assertEquals(favorites.size(), it.size());
            })
            .verifyComplete();
    }

    @Test
    public void getActorMovies() {
        Map<String, List<Movie>> movieByActor = movieRepo.getMoviesByActor();

        Mono<Map<String, List<Movie>>> map = Flux.fromIterable(movieByActor.keySet())
            .flatMap(actor -> solution.moviesForActor(actor)
                .collectList()
                .map(movies -> Tuples.of(actor, movies)))
            .collectMap(Tuple2::getT1, Tuple2::getT2);

        StepVerifier.create(map.timeout(Duration.ofSeconds(3)))

            .assertNext(it -> Assert.assertEquals(movieByActor, it))
            .verifyComplete()
        ;

    }

    @Test
    public void getUserInfo() {
        List<UserInfo> block = userRepo.getAllUsers()
            .map(User::getUserName)
            .flatMap(solution::getUserInfo)
            .collectList()
            .block();

        System.out.println(block);

    }
}
