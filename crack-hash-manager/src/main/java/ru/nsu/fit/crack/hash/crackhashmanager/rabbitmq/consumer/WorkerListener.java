package ru.nsu.fit.crack.hash.crackhashmanager.rabbitmq.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.worker.CrackHashWorkerResponse;
import ru.nsu.fit.crack.hash.crackhashmanager.service.TaskService;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkerListener {
    private final TaskService taskWorkerService;

    @RabbitListener(queues = "${application.queue-name-consumer}")
    public void listener(CrackHashWorkerResponse response) {
        log.info("Received worker response: {}", response.getAnswers().getWords());
        taskWorkerService.update(
            UUID.fromString(response.getRequestId()),
            response.getAnswers().getWords()
        );
    }
}
