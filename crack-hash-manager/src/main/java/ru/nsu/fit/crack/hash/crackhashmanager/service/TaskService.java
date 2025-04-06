package ru.nsu.fit.crack.hash.crackhashmanager.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.nsu.fit.crack.hash.crackhashmanager.configuration.properties.ApplicationProperties;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.client.HashRequestDto;
import ru.nsu.fit.crack.hash.crackhashmanager.excetion.TaskNotFoundException;
import ru.nsu.fit.crack.hash.crackhashmanager.model.Task;
import ru.nsu.fit.crack.hash.crackhashmanager.model.enums.TaskStatus;
import ru.nsu.fit.crack.hash.crackhashmanager.repository.TaskRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final ApplicationProperties applicationProperties;
    private final TaskRepository taskRepository;

    public Mono<Task> createTask(HashRequestDto request) {
        return Mono.fromSupplier(() -> new Task(UUID.randomUUID(), TaskStatus.IN_PROGRESS, request.hash(), request.maxLength()))
            .doOnNext(taskRepository::save);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void update(UUID id, List<String> words) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        log.info("Task ack {}, words {}", task.getAcknowledge(), words);
        if (task.getTaskStatus() == TaskStatus.IN_PROGRESS) {
            task.incrementAcknowledge();
            task.addWords(words);
            log.info("Add words {}", task.getWords());
        }
        if (task.getAcknowledge() == applicationProperties.numberWorkers()) {
            task.updateStatus(TaskStatus.READY);
        }
        taskRepository.save(task);
    }

    @Nonnull
    public Mono<Task> getTask(UUID id) {
        return Mono.fromSupplier(() -> taskRepository.getById(id));
    }
}
