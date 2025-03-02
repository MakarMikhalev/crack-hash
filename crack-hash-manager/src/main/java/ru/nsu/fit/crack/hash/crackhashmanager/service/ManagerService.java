package ru.nsu.fit.crack.hash.crackhashmanager.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.nsu.fit.crack.hash.crackhashmanager.configuration.properties.ApplicationProperties;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.client.HashRequestDto;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.client.IdResponseDto;
import ru.nsu.fit.crack.hash.crackhashmanager.model.Task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerService {
    private BlockingQueue<Task> queue;
    private final ApplicationProperties applicationProperties;
    private final DistributionService distributionService;
    private final TaskService taskService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @PostConstruct
    void init() {
        queue = new LinkedBlockingQueue<>(applicationProperties.sizeQueue());
        processing();
    }

    public Mono<IdResponseDto> addTask(HashRequestDto request) {
        return taskService.createTask(request)
            .doOnSuccess(t -> queue.add(t))
            .map(t ->
                IdResponseDto.builder()
                .requestId(t.getId())
                .build()
            );
    }

    public void processing() {
        executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    distributionService.distribution(queue.take());
                } catch (InterruptedException e) {
                    log.warn("Failed to complete task");
                }
            }
        });
    }

    @PreDestroy
    void shutdown() {
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
