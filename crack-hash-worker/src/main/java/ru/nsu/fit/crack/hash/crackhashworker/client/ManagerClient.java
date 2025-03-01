package ru.nsu.fit.crack.hash.crackhashworker.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.nsu.fit.crack.hash.crackhashworker.dto.CrackHashWorkerResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class ManagerClient {
    private final WebClient webClient;

    public Mono<Void> sendResultManager(CrackHashWorkerResponse crackHashWorkerResponse) {
        return webClient.patch()
            .uri("/internal/api/manager/hash/crack/request")
            .bodyValue(crackHashWorkerResponse)
            .retrieve()
            .bodyToMono(Void.class)
            .doOnError(error -> log.warn("An error has occurred {}", error.getMessage()));
    }
}
