package ru.nsu.fit.crack.hash.crackhashmanager.configuration.properties;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
public record ApplicationProperties(
    @NotNull Integer numberWorkers,
    @NotNull String alphabet,
    @NotNull String domainWorker,
    @NotNull int processingTime
) {
}
