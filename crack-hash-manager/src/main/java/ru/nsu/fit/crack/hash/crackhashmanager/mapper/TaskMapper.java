package ru.nsu.fit.crack.hash.crackhashmanager.mapper;

import org.springframework.stereotype.Component;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.client.TaskDto;
import ru.nsu.fit.crack.hash.crackhashmanager.model.Task;

@Component
public class TaskMapper {
    public TaskDto toDto(double percentCalculate, Task task) {
        return TaskDto.builder()
            .id(task.getId())
            .words(task.getWords())
            .acknowledge(task.getAcknowledge())
            .taskStatus(task.getTaskStatus())
            .percentCalculate(percentCalculate)
            .hash(task.getHash())
            .maxLength(task.getMaxLength())
            .build();
    }
}
