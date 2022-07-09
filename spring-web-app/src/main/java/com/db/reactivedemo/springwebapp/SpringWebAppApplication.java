package com.db.reactivedemo.springwebapp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@RestController
@Slf4j
@Lazy(false)
public class SpringWebAppApplication {
    @Value("${downstream.service}")
    private String downstreamService;

    @Value(value = "${service.name}")
    private String serviceName;

    private final RestTemplate client = new RestTemplate();

//    @PostConstruct
//    public void warmup() {
//        if (Objects.equals(downstreamService, "_")) {
//            return;
//        }
//        for (int i = 0; i < 10; i++) {
//            try {
//                Thread.sleep(100);
//                BlockingResponse blockingResponse = new RestTemplate().getForObject(downstreamService + "/doIt", BlockingResponse.class, Collections.emptyList());
////                BlockingResponse blockingResponse = new RestTemplate().getForObject(downstreamService + "/100", BlockingResponse.class);
//                log.info(String.format("Warmup response: %s", blockingResponse));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public static void main(String[] args) {
        SpringApplication.run(SpringWebAppApplication.class, args);
    }


    @GetMapping("/doIt")
    public List<BlockingResponse> doIt(@RequestParam("latencies") ArrayList<Integer> latencies) {
        if (latencies.isEmpty() || downstreamService.equals("_")) {
            return Collections.singletonList(BlockingResponse.builder()
                .serviceName(serviceName)
                .requestTime(LocalDateTime.now())
                .responseTime(LocalDateTime.now())
                .delay(0)
                .build());
        }

        HashMap<String, Object> params = createParams(latencies);

        LocalDateTime requestStart = LocalDateTime.now();
        String url = downstreamService + "/doIt?latencies={latencies}";
        BlockingResponse[] forObject = client.getForEntity(
                url,
                BlockingResponse[].class,
                params
            )
            .getBody();

        BlockingResponse response = BlockingResponse.builder()
            .serviceName(serviceName)
            .requestTime(requestStart)
            .responseTime(LocalDateTime.now())
            .delay(latencies.get(0))
            .build();
        return Stream.concat(Arrays.stream(forObject), Stream.of(response))
            .collect(Collectors.toList());
    }

    private HashMap<String, Object> createParams(ArrayList<Integer> latencies) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("latencies", latencies.stream()
            .skip(1)
            .map(Object::toString)
            .collect(Collectors.joining(",")));
        return params;
    }

  /*  @GetMapping("/delay/{delayMillis}")
    public BlockingMetrics delay(@PathVariable int delayMillis) {
        LocalDateTime requestStart = LocalDateTime.now();
        BlockingResponse result = client.getForObject(downstreamService + "/" + delayMillis, BlockingResponse.class);
        LocalDateTime now = LocalDateTime.now();
        return BlockingMetrics.builder()
            .delay(delayMillis)
            .requestTime(requestStart)
            .responseTime(now)
            .responses(Collections.singletonList(result))
            .actual(milliTime(requestStart, now))
            .build();
    }

    @GetMapping("/data/{delayMillis}")
    public BlockingResponse data(@PathVariable int delayMillis) {
        LocalDateTime now = LocalDateTime.now();
        work(delayMillis);
        return new BlockingResponse(
            serviceName,
            delayMillis,
            now,
            LocalDateTime.now()
        );
    }*/

    private static void work(long delayMillis) {
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int milliTime(LocalDateTime start, LocalDateTime stop) {
        return (int) (toEpocMilli(stop) - toEpocMilli(start));
    }

    private static long toEpocMilli(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class BlockingResponse {
        String serviceName;
        int delay;
        LocalDateTime requestTime;
        LocalDateTime responseTime;
    }

    @lombok.Value
    @Builder
    static class BlockingMetrics {
        int delay;
        int actual;
        LocalDateTime requestTime;
        LocalDateTime responseTime;
        List<BlockingResponse> responses;
    }
}
