package ru.nsu.fit.crack.hash.crackhashmanager.service;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.nsu.fit.crack.hash.crackhashmanager.configuration.properties.ApplicationProperties;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.client.HashRequestDto;
import ru.nsu.fit.crack.hash.crackhashmanager.model.Task;
import ru.nsu.fit.crack.hash.crackhashmanager.model.enums.TaskStatus;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TaskService {
    @Getter
    private final ConcurrentHashMap<UUID, Task> tasks = new ConcurrentHashMap<>();
    private final ApplicationProperties applicationProperties;

    public Mono<Task> createTask(HashRequestDto request) {
        return Mono.fromSupplier(() -> new Task(TaskStatus.IN_PROGRESS, request.hash(), request.maxLength()))
            .doOnNext(task -> tasks.put(task.getId(), task));
    }

    public void update(UUID id, List<String> words) {
        tasks.merge(id, new Task(TaskStatus.ERROR, "", 0), (oldValue, newValue) -> {
            if (oldValue.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                oldValue.incrementAcknowledge();
                oldValue.addWords(words);
            }
            if (oldValue.getAcknowledge() == applicationProperties.numberWorkers()) {
                oldValue.updateStatus(TaskStatus.READY);
            }
            return oldValue;
        });
    }

    @Nonnull
    public Mono<Task> getTask(UUID id) {
        return Mono.fromSupplier(() -> tasks.get(id));
    }
}
