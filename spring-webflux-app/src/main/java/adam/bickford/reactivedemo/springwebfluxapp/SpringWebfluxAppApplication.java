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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        int delay = latencies.get(0);
        boolean callDownstream = latencies.size() > 1 && !downstreamService.equals("_");
        return Mono.just(Instant.now())
            .delayElement(Duration.ofMillis(delay))
            .flatMap(start -> getDownstreamResults(latencies, callDownstream)
                .flatMap(downstreamResults -> getMetrics(start, delay)
                    .map(metrics -> Stream.concat(
                            downstreamResults.stream(),
                            metrics.stream())
                        .collect(Collectors.toList()))));
    }

    private Mono<? extends List<BlockingResponse>> getDownstreamResults(ArrayList<Integer> latencies, boolean callDownstream) {
        return callDownstream
            ? downstreamCall(latencies)
            : Mono.just(Collections.emptyList());
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

    private Mono<List<BlockingResponse>> getMetrics(Instant start, Integer delay) {
        Instant now = Instant.now();
        return Mono.just(Collections.singletonList(BlockingResponse.builder()
            .serviceName(serviceName)
            .requestTime(start)
            .responseTime(now)
            .delay(delay)
            .actual(getActual(start, now))
            .build()));
    }

    private int getActual(Instant start, Instant end) {
        return (int) (end.toEpochMilli() - start.toEpochMilli());
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class BlockingResponse {
        String serviceName;
        int delay;
        int actual;
        Instant requestTime;
        Instant responseTime;
    }
}
