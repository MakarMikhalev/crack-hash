package ru.nsu.fit.crack.hash.crackhashmanager.model;

import lombok.Getter;
import ru.nsu.fit.crack.hash.crackhashmanager.model.enums.TaskStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Task {
    private final String hash;
    private TaskStatus taskStatus;
    private final UUID id = UUID.randomUUID();
    private final Instant created = Instant.now();
    private int acknowledge = 0;
    private final List<String> words = new ArrayList<>();

    public Task(TaskStatus taskStatus, String hash) {
        this.taskStatus = taskStatus;
        this.hash = hash;
    }

    public void addWords(List<String> words) {
        this.words.addAll(words);
    }

    public void updateStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void incrementAcknowledge() {
        ++acknowledge;
    }
}
