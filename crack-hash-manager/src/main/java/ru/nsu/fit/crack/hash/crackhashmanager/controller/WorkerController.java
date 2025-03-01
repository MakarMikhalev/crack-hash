package ru.nsu.fit.crack.hash.crackhashmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.worker.CrackHashWorkerResponse;
import ru.nsu.fit.crack.hash.crackhashmanager.service.TaskService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class WorkerController {
    private final TaskService taskService;

    @PatchMapping("/internal/api/manager/hash/crack/request")
    public void handleWord(@RequestBody CrackHashWorkerResponse crackHashWorkerResponse) {
        taskService.update(
            UUID.fromString(crackHashWorkerResponse.getRequestId()),
            crackHashWorkerResponse.getAnswers().getWords()
        );
    }
}
