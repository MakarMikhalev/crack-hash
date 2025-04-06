package ru.nsu.fit.crack.hash.crackhashmanager.sheduler;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.nsu.fit.crack.hash.crackhashmanager.configuration.properties.ApplicationProperties;
import ru.nsu.fit.crack.hash.crackhashmanager.model.enums.TaskStatus;
import ru.nsu.fit.crack.hash.crackhashmanager.repository.TaskRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TaskTimeoutScheduler implements ApplicationRunner {
    private final ApplicationProperties applicationProperties;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final TaskRepository taskRepository;

    @Override
    public void run(ApplicationArguments args) {
        scheduler.scheduleAtFixedRate(() -> taskRepository.findAll()
            .stream()
            .filter(task -> task.getTaskStatus() == TaskStatus.IN_PROGRESS)
            .forEach(ticket -> {
            final long duration = Duration.between(ticket.getCreated(), LocalDateTime.now()).getSeconds();
            if(duration > applicationProperties.processingTime()) ticket.updateStatus(TaskStatus.ERROR);
        }), 0, applicationProperties.processingTime(), TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdownScheduler() {
        scheduler.shutdown();
    }
}
