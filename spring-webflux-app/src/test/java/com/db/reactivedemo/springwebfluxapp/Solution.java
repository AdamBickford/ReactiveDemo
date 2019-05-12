package com.db.reactivedemo.springwebfluxapp;

import com.db.reactivedemo.springwebfluxapp.movie.Favorites;
import com.db.reactivedemo.springwebfluxapp.movie.History;
import com.db.reactivedemo.springwebfluxapp.movie.Movie;
import com.db.reactivedemo.springwebfluxapp.user.User;
import com.db.reactivedemo.springwebfluxapp.user.UserInfo;
import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
public class Solution {

    private WebClient webClient = WebClient.builder()
        .defaultHeader("Accept", "application/stream+json")
        .build();

    public Mono<User> getUserByName(String userName) {
        return webClient.get()
            .uri("http://localhost:8090/user/{userName}", userName)
            .retrieve()
            .bodyToMono(User.class)
            ;
    }

    public Mono<Favorites> getFavoritesForUser(String userName) {
        return webClient.get()
            .uri("http://localhost:8090/favorites/{userName}", userName)
            .retrieve()
            .bodyToMono(Favorites.class)
//            .doOnNext(it -> System.out.println("favorites for " + userName + " " + ))
            .retry(100)
            ;
    }

    public Flux<Movie> moviesForActor(String actor) {
        return moviesForActor("http://localhost:8090/actor/{userName}", actor)
            .timeout(Duration.ofMillis(800))
            .doOnError(TimeoutException.class, it -> System.out.println("Slow response, using failover url"))
            .onErrorResume(
                TimeoutException.class,
                it -> moviesForActor("http://localhost:8090/actor2/{userName}", actor)
            );
    }

    private Flux<Movie> moviesForActor(String url, String actor) {
        return webClient.get()
            .uri(url, actor)
            .retrieve()
            .bodyToFlux(Movie.class)
            ;
    }

    public Mono<History> historyForUser(String userName) {
        return webClient.get()
            .uri("http://localhost:8090/history/{userName}", userName)
            .retrieve()
            .bodyToMono(History.class)
            ;
    }

    public Flux<Movie> getAll() {
        return webClient.get()
            .uri("http://localhost:8090/all")
            .retrieve()
            .bodyToFlux(Movie.class)
            ;
    }

    public Flux<Movie> recommendedMovies(String userName) {
        return Mono.just(userName)
            .flatMap(this::getUserByName)
            .flatMap(user -> Mono.zip(
                getFavoritesForUser(user.getUserName()),
                historyForUser(user.getUserName())))
            .flatMapMany(t2 -> recommendedMovies(t2.getT1(), t2.getT2()))
            ;
    }


    public Flux<Movie> recommendedMovies(Favorites favorites, History history) {
        return Flux.fromIterable(favorites.getMovies())
            .flatMap(movie -> Flux.fromIterable(movie.getActors()))
            .flatMap(this::moviesForActor)
            .concatWith(getAll())
            .filter(movie -> !history.getMovies().contains(movie))
            .distinct()
            .take(3);
    }

    @Test
    public Mono<UserInfo> getUserInfo(String userName) {
        return getUserByName(userName)
            .doOnNext(it -> System.out.println("getting info " + it))
            .flatMap(user -> Mono.zip(
                Mono.just(user),
                getFavoritesForUser(user.getUserName()),
                historyForUser(user.getUserName()))
                .flatMap(t3 -> recommendedMovies(t3.getT2(), t3.getT3())
                    .collectList()
                    .map(recommended -> UserInfo.builder()
                        .history(t3.getT3())
                        .favorites(t3.getT2())
                        .user(user)
                        .recommended(recommended)
                        .build())
                )
            );
    }
}
