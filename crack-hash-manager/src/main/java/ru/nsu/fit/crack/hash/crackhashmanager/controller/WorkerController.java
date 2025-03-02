package ru.nsu.fit.crack.hash.crackhashmanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.worker.CrackHashWorkerResponse;
import ru.nsu.fit.crack.hash.crackhashmanager.service.TaskService;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class WorkerController {
    private final TaskService taskWorkerService;

    @PatchMapping("/internal/api/manager/hash/crack/request")
    public void handleWord(@RequestBody CrackHashWorkerResponse response) {
        log.info("Получен ответ от worker {}, слова {}", response.getPartNumber(), response.getAnswers().getWords());
        taskWorkerService.update(
            UUID.fromString(response.getRequestId()),
            response.getAnswers().getWords()
        );
    }
}
