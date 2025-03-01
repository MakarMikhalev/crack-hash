package ru.nsu.fit.crack.hash.crackhashmanager.dto.client;

public record HashRequestDto(
    String hash,
    int maxLength
) {
}
