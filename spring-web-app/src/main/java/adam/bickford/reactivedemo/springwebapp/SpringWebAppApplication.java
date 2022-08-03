package adam.bickford.reactivedemo.springwebapp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
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

    public static void main(String[] args) {
        SpringApplication.run(SpringWebAppApplication.class, args);
    }

    @PostConstruct
    public void stuff() {
        log.error("adam startup: " + downstreamService);
    }

    @GetMapping("/request")
    public List<BlockingResponse> request(@RequestParam("latencies") ArrayList<Integer> latencies) {
        Integer delay = latencies.get(0);
        LocalDateTime start = LocalDateTime.now();

        work(delay);

        if (latencies.size() == 1 || downstreamService.equals("_")) {
            return getMetrics(delay, start);
        }

        return Stream.concat(
                downstreamCall(latencies),
                getMetrics(delay, start).stream()
            )
            .collect(Collectors.toList());
    }

    private List<BlockingResponse> getMetrics(Integer delay, LocalDateTime start) {
        LocalDateTime now = LocalDateTime.now();
        return Collections.singletonList(BlockingResponse.builder()
            .serviceName(serviceName)
            .requestTime(start)
            .responseTime(now)
            .delay(delay)
            .actual(getActual(start, now))
            .build());
    }

    private Stream<BlockingResponse> downstreamCall(ArrayList<Integer> latencies) {
        System.out.println("making request to " + downstreamService + "/request?latencies={latencies}");
        return Arrays.stream(Objects.requireNonNull(
            client.getForEntity(
                    downstreamService + "/request?latencies={latencies}",
                    BlockingResponse[].class,
                    createParams(latencies)
                )
                .getBody()
        ));
    }

    private int getActual(LocalDateTime start, LocalDateTime end) {
        return (int) ((end.toLocalTime().toNanoOfDay() - start.toLocalTime().toNanoOfDay()) / 1_000_000);
    }

    private HashMap<String, Object> createParams(ArrayList<Integer> latencies) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("latencies", latencies.stream()
            .skip(1)
            .map(Object::toString)
            .collect(Collectors.joining(",")));
        return params;
    }

    private static void work(long delayMillis) {
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
