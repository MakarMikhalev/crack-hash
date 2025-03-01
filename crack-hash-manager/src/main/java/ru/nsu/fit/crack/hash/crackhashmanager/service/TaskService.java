package ru.nsu.fit.crack.hash.crackhashmanager.service;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.nsu.fit.crack.hash.crackhashmanager.configuration.properties.ApplicationProperties;
import ru.nsu.fit.crack.hash.crackhashmanager.model.Task;
import ru.nsu.fit.crack.hash.crackhashmanager.model.enums.TaskStatus;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {
    @Getter
    private final ConcurrentHashMap<UUID, Task> tasks = new ConcurrentHashMap<>();
    private final ApplicationProperties applicationProperties;

    public TaskService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;

        UUID uuid1 = UUID.fromString("f8062f6f-0bef-4fe2-b1ee-462767d1cca3");
        tasks.put(uuid1, new Task(TaskStatus.IN_PROGRESS, "2eh900921"));

        UUID uuid2 = UUID.fromString("d8062f6f-0bef-4fe2-b1ee-462767d1cca3");
        tasks.put(uuid2, new Task(TaskStatus.ERROR, "2eh900921"));

        UUID uuid3 = UUID.fromString("c8062f6f-0bef-4fe2-b1ee-462767d1cca3");
        tasks.put(uuid3, new Task(TaskStatus.READY, "2eh900921"));
    }

    public Mono<Task> createTask(String hash) {
        return Mono.fromSupplier(() -> new Task(TaskStatus.IN_PROGRESS, hash))
            .doOnNext(task -> tasks.put(task.getId(), task));
    }

    public void update(UUID id, List<String> words) {
        tasks.merge(id, new Task(TaskStatus.ERROR, tasks.get(id).getHash()), (oldValue, newValue) -> {
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
