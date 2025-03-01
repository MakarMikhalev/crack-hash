package ru.nsu.fit.crack.hash.crackhashmanager.dto.client;

import lombok.Builder;

import java.util.UUID;

@Builder
public record IdResponseDto(UUID requestId) {
}
