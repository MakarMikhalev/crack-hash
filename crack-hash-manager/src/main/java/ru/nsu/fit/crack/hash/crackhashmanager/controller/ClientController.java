package ru.nsu.fit.crack.hash.crackhashmanager.controller;

import lombok.RequiredArgsConstructor;
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
import ru.nsu.fit.crack.hash.crackhashmanager.service.TaskService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class ClientController {
    private final DistributionService distributionService;
    private final TaskService taskService;

    @PostMapping("/api/hash/crack")
    public ResponseEntity<Mono<IdResponseDto>> hashHacking(@RequestBody HashRequestDto hashRequestDto) {
        return ResponseEntity.ok(distributionService.distribution(hashRequestDto));
    }

    @GetMapping("/api/hash/status/{requestId}")
    public ResponseEntity<Mono<Task>> getTask(@PathVariable UUID requestId) {
        return ResponseEntity.ok(taskService.getTask(requestId));
    }
}
