package ru.nsu.fit.crack.hash.crackhashmanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.client.HashRequestDto;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.client.IdResponseDto;
import ru.nsu.fit.crack.hash.crackhashmanager.model.Task;
import ru.nsu.fit.crack.hash.crackhashmanager.service.DistributionService;
import ru.nsu.fit.crack.hash.crackhashmanager.service.ManagerService;
import ru.nsu.fit.crack.hash.crackhashmanager.service.TaskService;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ClientController {
    private final TaskService taskWorkerService;
    private final ManagerService managerService;

    @PostMapping("/api/hash/crack")
    public ResponseEntity<Mono<IdResponseDto>> hashHacking(@RequestBody HashRequestDto hashRequestDto) {
        log.info("hashHacking {}", hashRequestDto);
        return ResponseEntity.ok(managerService.addTask(hashRequestDto));
    }

    @GetMapping("/api/hash/status/{requestId}")
    public ResponseEntity<Mono<Task>> getTask(@PathVariable UUID requestId) {
        log.info("get task request id {}", requestId);
        return ResponseEntity.ok(taskWorkerService.getTask(requestId));
    }

    @GetMapping
    public ResponseEntity<Void> healChecker() {
        return ResponseEntity.ok().build();
    }
}
