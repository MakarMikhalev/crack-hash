package ru.nsu.fit.crack.hash.crackhashworker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.nsu.fit.crack.hash.crackhashworker.dto.CrackHashManagerRequest;
import ru.nsu.fit.crack.hash.crackhashworker.service.TaskService;

@RestController
@RequiredArgsConstructor
public class ManagerController {
    private final TaskService taskService;

    @PostMapping("/internal/api/worker/hash/crack/task")
    public ResponseEntity<Mono<Void>> calcHash(@RequestBody CrackHashManagerRequest crackHashManagerRequest) {
        return ResponseEntity.ok(taskService.runTask(crackHashManagerRequest));
    }
}
