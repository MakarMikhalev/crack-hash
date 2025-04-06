package ru.nsu.fit.crack.hash.crackhashmanager.excetion;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(UUID id) {
        super("Task not found: %s".formatted(id));
    }
}
