package ru.nsu.fit.crack.hash.crackhashmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.nsu.fit.crack.hash.crackhashmanager.configuration.properties.ApplicationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class CrackHashManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrackHashManagerApplication.class, args);
    }
}
