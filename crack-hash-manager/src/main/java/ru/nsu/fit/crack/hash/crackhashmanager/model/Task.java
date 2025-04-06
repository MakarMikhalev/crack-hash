package ru.nsu.fit.crack.hash.crackhashmanager.model;

import lombok.Getter;
import lombok.Setter;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.nsu.fit.crack.hash.crackhashmanager.model.enums.TaskStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Document(collection = "task")
public class Task {
    @Id
    private final UUID id;

    private final String hash;

    private final int maxLength;

    private TaskStatus taskStatus;

    private final Instant created = Instant.now();

    private int acknowledge = 0;

    private List<String> words = new ArrayList<>();

    public Task(UUID id, TaskStatus taskStatus, String hash, int maxLength) {
        this.maxLength = maxLength;
        this.taskStatus = taskStatus;
        this.hash = hash;
        this.id = id;
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
