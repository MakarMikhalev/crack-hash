package ru.nsu.fit.crack.hash.crackhashmanager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nsu.fit.crack.hash.crackhashmanager.model.Task;

import java.util.UUID;

public interface TaskRepository extends MongoRepository<Task, UUID> {
    <T> T getById(UUID id);
}
