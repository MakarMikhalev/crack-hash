package ru.nsu.fit.crack.hash.crackhashworker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.nsu.fit.crack.hash.crackhashworker.client.ManagerClient;
import ru.nsu.fit.crack.hash.crackhashworker.dto.CrackHashManagerRequest;
import ru.nsu.fit.crack.hash.crackhashworker.mapper.TaskMapper;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final ManagerClient managerClient;

    public Mono<Void> runTask(CrackHashManagerRequest request) {
        return Mono.fromCallable(() -> TaskMapper.mapRequest(request).call())
            .subscribeOn(Schedulers.boundedElastic())
            .map(words -> TaskMapper.mapResponse(request, words))
            .flatMap(managerClient::sendResultManager)
            .then();
    }
}
