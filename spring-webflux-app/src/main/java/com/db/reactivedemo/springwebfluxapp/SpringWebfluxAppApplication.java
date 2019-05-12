package com.db.reactivedemo.springwebfluxapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@RestController
@Slf4j
public class SpringWebfluxAppApplication {
//    @Value("${downstream.service}")
//    private String downstreamService;
//
//    @Value("${service.name}")
//    private String serviceName;

    private final WebClient webClient = WebClient.builder().build();

    public static void main(String[] args) {
        SpringApplication.run(SpringWebfluxAppApplication.class, args);
    }

//    @PostConstruct
//    public void init() {
//        new Thread(() -> {
//            for (int i = 0; i < 10; i++) {
//            try {
//                Thread.sleep(100);
//                String forObject = new RestTemplate().getForObject(downstreamService + "/100", String.class);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            }
//
//        }).start();
//    }

//    @GetMapping("/{delayMillis}")
//    public Mono<String> delay(@PathVariable long delayMillis) {
//        return webClient.get()
//            .uri(downstreamService + "/" + delayMillis)
//            .exchange()
//            .delayElement(Duration.ofMillis(delayMillis))
//            .flatMap(response -> response.bodyToMono(String.class))
//            .map(result -> String.format("hop (%s) -> %s", serviceName, result));
//    }
//
//    AtomicInteger count = new AtomicInteger(0);
//
//    @GetMapping("/data/{delayMillis}")
//    public Mono<String> data(@PathVariable long delayMillis) {
////        log.info("request " + count.getAndIncrement() + " -- " + delayMillis);
//        return Mono.just("Reactive: ")
////        return Mono.just("Reactive: " + UUID.randomUUID().toString())
//            .delayElement(Duration.ofMillis(delayMillis))
//            ;
//    }


}
