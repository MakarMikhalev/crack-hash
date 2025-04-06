package ru.nsu.fit.crack.hash.crackhashmanager.excetion;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
public record ErrorDto(
    Instant createdAt,

    int code,

    String message,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, String> errors
) {
}
