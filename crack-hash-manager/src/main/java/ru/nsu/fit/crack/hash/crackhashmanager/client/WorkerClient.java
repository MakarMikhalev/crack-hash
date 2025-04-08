package ru.nsu.fit.crack.hash.crackhashmanager.client;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.nsu.fit.crack.hash.crackhashmanager.configuration.properties.ApplicationProperties;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.worker.CrackHashManagerRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkerClient {
    private final Map<Integer, String> mapWorkersUrl = new HashMap<>();
    private final WebClient webClient;
    private final ApplicationProperties applicationProperties;

    @PostConstruct
    void init() {
        log.info("List url workers {}", applicationProperties.domainWorkers());
        AtomicInteger index = new AtomicInteger(0);
        applicationProperties.domainWorkers()
            .forEach(url -> mapWorkersUrl.put(index.getAndIncrement(), url));
    }

    public void send(Integer numberWorker, CrackHashManagerRequest crackHashManagerRequest) {
        String url = "%s/internal/api/worker/hash/crack/task".formatted(mapWorkersUrl.get(numberWorker));
        Mono<Void> mono = webClient.post()
            .uri(url)
            .bodyValue(crackHashManagerRequest)
            .retrieve()
            .bodyToMono(Void.class)
            .doOnSuccess(response -> log.info("Запрос успешно отправлен"))
            .doOnError(error -> log.error("Ошибка при отправке запроса: {}", error.getMessage()))
            .onErrorResume(error -> Mono.empty());
        mono.subscribeOn(Schedulers.boundedElastic()).subscribe();
    }

    public Mono<List<Integer>> getNumberCalculatedWord(String keyTask) {
        String url = "%s/internal/api/worker/hash/task/count/%s";
        return Flux.fromIterable(mapWorkersUrl.values())
            .flatMap(worker -> {
                    log.info("url {}", url.formatted(worker, keyTask));
                    return webClient.get()
                        .uri(url.formatted(worker, keyTask))
                        .retrieve()
                        .bodyToMono(Integer.class);
                }
            )
            .collectList();
    }
}
