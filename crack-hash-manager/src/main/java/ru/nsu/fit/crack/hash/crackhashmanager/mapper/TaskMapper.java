package ru.nsu.fit.crack.hash.crackhashmanager.mapper;

import org.springframework.stereotype.Component;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.client.TaskDto;
import ru.nsu.fit.crack.hash.crackhashmanager.model.Task;

@Component
public class TaskMapper {
    private static final String TEMPLATE_PERCENT = "%d%%";

    public TaskDto toDto(long percentCalculate, Task task) {
        return TaskDto.builder()
            .id(task.getId())
            .words(task.getWords())
            .acknowledge(task.getAcknowledge())
            .taskStatus(task.getTaskStatus())
            .percentCalculate(TEMPLATE_PERCENT.formatted(percentCalculate))
            .hash(task.getHash())
            .maxLength(task.getMaxLength())
            .build();
    }
}
