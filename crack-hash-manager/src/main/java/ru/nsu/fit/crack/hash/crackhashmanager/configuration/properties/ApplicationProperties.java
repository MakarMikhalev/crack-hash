package ru.nsu.fit.crack.hash.crackhashmanager.configuration.properties;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "application")
public record ApplicationProperties(
    @NotNull Integer numberWorkers,
    @NotNull String alphabet,
    @NotNull List<String> domainWorkers,
    @NotNull int processingTime,
    @NotNull int sizeQueue
) {
}
