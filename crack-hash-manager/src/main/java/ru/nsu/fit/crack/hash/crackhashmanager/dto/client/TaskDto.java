package ru.nsu.fit.crack.hash.crackhashmanager.dto.client;

import lombok.Builder;
import ru.nsu.fit.crack.hash.crackhashmanager.model.enums.TaskStatus;

import java.util.List;
import java.util.UUID;

@Builder
public record TaskDto(
    UUID id,
    String hash,
    int maxLength,
    TaskStatus taskStatus,
    int acknowledge,
    List<String> words,
    double percentCalculate
) {
}
