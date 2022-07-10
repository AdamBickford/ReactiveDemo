package adam.bickford.reactivedemo.springwebfluxapp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@RestController
@Slf4j
public class SpringWebfluxAppApplication {
    @Value("${downstream.service:_}")
    private String downstreamService;

    @Value("${service.name}")
    private String serviceName;

    private final WebClient webClient = WebClient.builder().build();

    public static void main(String[] args) {
        SpringApplication.run(SpringWebfluxAppApplication.class, args);
    }

    @GetMapping("/request")
    public Mono<List<BlockingResponse>> request(@RequestParam("latencies") ArrayList<Integer> latencies) {
        return Mono.just(latencies.get(0))
            .flatMap(delay -> {
                LocalDateTime now = LocalDateTime.now();
                return work(delay).thenReturn(now)
                    //if there's no downstream service or this is the last defined latency
                    //consider it the end of the chain and return and empty list to merge
                    //with our own metrics
                    .then((((latencies.size() == 1) || downstreamService.equals("_"))
                        ? getMetrics(now, delay)
                        : downstreamCall(latencies).zipWith(getMetrics(now, delay)).map(it -> {
                        ArrayList<BlockingResponse> blockingResponses = new ArrayList<>();
                        blockingResponses.addAll(it.getT1());
                        blockingResponses.addAll(it.getT2());
                        return blockingResponses;
                    })));
            });
    }

    private static Mono<Long> work(long delayMillis) {
        return Mono.delay(Duration.ofMillis(delayMillis))
            .thenReturn(delayMillis);
    }

    private Mono<List<BlockingResponse>> downstreamCall(ArrayList<Integer> latencies) {
        String uri = downstreamService + "/request?latencies=" + latencies.stream()
            .skip(1)
            .map(Object::toString)
            .collect(Collectors.joining(","));

        return webClient.get()
            .uri(uri)
            .retrieve()
            .bodyToMono(BlockingResponse[].class)
            .single()
            .map(Arrays::asList);
    }

    private Mono<List<BlockingResponse>> getMetrics(LocalDateTime start, Integer delay) {
        LocalDateTime now = LocalDateTime.now();
        return Mono.just(Collections.singletonList(BlockingResponse.builder()
            .serviceName(serviceName)
            .requestTime(start)
            .responseTime(now)
            .delay(delay)
            .actual(getActual(start, now))
            .build()));
    }

    private int getActual(LocalDateTime start, LocalDateTime end) {
        return (int) ((end.toLocalTime().toNanoOfDay() - start.toLocalTime().toNanoOfDay()) / 1_000_000);
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class BlockingResponse {
        String serviceName;
        int delay;
        int actual;
        LocalDateTime requestTime;
        LocalDateTime responseTime;
    }
}
