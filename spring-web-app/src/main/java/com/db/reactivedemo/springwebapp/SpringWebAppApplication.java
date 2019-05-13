package com.db.reactivedemo.springwebapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@RestController
@Slf4j
@Lazy(false)
public class SpringWebAppApplication {
    @Value("${downstream.service}")
    private String downstreamService;

    @Value("${service.name}")
    private String serviceName;

    private final RestTemplate client = new RestTemplate();

    @PostConstruct
    public void init() {
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(100);
                    String forObject = new RestTemplate().getForObject(downstreamService + "/100", String.class);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringWebAppApplication.class, args);

    }

    @GetMapping("/{delayMillis}")
    public String delay(@PathVariable long delayMillis) {
        work(delayMillis);
        String result = client.getForObject(downstreamService + "/" + delayMillis, String.class);
        return String.format("hop (%s) -> %s", serviceName, result);
    }

    AtomicInteger count = new AtomicInteger(0);

    @GetMapping("/data/{delayMillis}")
    public String data(@PathVariable long delayMillis) {
//        log.info("request " + count.incrementAndGet() + " -- " + delayMillis);
        work(delayMillis);
        return "Blocking: " + count;
//        return "Blocking: " + ThreadLocalRandom.current().nextInt();//UUID.randomUUID().toString();
    }

    private void work(long delayMillis) {
//        ThreadLocalRandom rng = ThreadLocalRandom.current();
//            Thread.sleep(delayMillis);
//        doWork1(delayMillis, rng.nextLong(), rng.nextLong(), rng.nextLong());
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
