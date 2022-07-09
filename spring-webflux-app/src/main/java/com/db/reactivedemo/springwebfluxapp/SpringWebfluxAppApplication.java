package com.db.reactivedemo.springwebfluxapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@RestController
@Slf4j
public class SpringWebfluxAppApplication {
    @Value("${downstream.service}")
    private String downstreamService;

    @Value("${service.name}")
    private String serviceName;



    private final WebClient webClient = WebClient.builder().build();

    public static void main(String[] args) {
        SpringApplication.run(SpringWebfluxAppApplication.class, args);
    }

    @GetMapping("/proxy/{delayMillis}")
    public Mono<String> proxy(@PathVariable long delayMillis) {
        return webClient.get()
            .uri(downstreamService + "/data/" + delayMillis)
            .retrieve()
            .bodyToMono(String.class)
            .map(result -> String.format("hop (%s) -> %s", serviceName, result))
            ;
    }

    AtomicInteger count = new AtomicInteger(0);
    @GetMapping("/data/{delayMillis}")
    public Mono<String> data(@PathVariable long delayMillis) {
        return Mono.just(LocalDateTime.now())
            .delayElement(Duration.ofMillis(delayMillis))
            .map(now -> String.format("Start: %s, Stop: %s, Blocked: %s", now, LocalDateTime.now(), count.get()))
            ;
    }
}
