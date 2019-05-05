package com.db.reactivedemo.springwebapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@SpringBootApplication
@RestController
@Slf4j
public class SpringWebAppApplication {
    @Value("${downstream.service}")
    private String downstreamService;

    @Value("${service.name}")
    private String serviceName;

    private final RestTemplate client = new RestTemplate();

    public static void main(String[] args) {
        SpringApplication.run(SpringWebAppApplication.class, args);
    }

    @GetMapping("/{delayMillis}")
    public String delay(@PathVariable long delayMillis) {
        work(delayMillis);
        String result = client.getForObject(downstreamService + "/" + delayMillis, String.class);
        return String.format("hop (%s) -> %s", serviceName, result);
    }


    @GetMapping("/data/{delayMillis}")
    public String data(@PathVariable long delayMillis) {
        work(delayMillis);
        return "Blocking: " + UUID.randomUUID().toString();
    }

    private void work(long delayMillis) {
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            log.error("Thread interrupted", e);
        }
    }
}
