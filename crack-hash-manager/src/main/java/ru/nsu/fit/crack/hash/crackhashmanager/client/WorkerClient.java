package ru.nsu.fit.crack.hash.crackhashmanager.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.worker.CrackHashManagerRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkerClient {
    private final WebClient webClient;

    public Mono<Void> send(CrackHashManagerRequest crackHashManagerRequest) {
        return webClient.post()
            .uri("/internal/api/worker/hash/crack/task")
            .bodyValue(crackHashManagerRequest)
            .retrieve()
            .bodyToMono(Void.class)
            .doOnError(error -> log.error("An error has occurred {}", error.getMessage()));
    }
}
