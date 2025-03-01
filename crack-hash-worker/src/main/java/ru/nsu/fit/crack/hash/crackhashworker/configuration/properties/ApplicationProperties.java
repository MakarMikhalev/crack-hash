package ru.nsu.fit.crack.hash.crackhashworker.configuration.properties;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
public record ApplicationProperties(
    @NotNull String domainManager,
    @NotNull int processingTime
) {
}
