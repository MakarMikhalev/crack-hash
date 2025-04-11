package ru.nsu.fit.crack.hash.crackhashworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.nsu.fit.crack.hash.crackhashworker.configuration.properties.ApplicationProperties;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class CrackHashWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrackHashWorkerApplication.class, args);
    }
}
