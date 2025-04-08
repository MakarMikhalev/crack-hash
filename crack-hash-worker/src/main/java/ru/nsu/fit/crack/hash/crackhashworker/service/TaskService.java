package ru.nsu.fit.crack.hash.crackhashworker.service;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.nsu.fit.crack.hash.crackhashworker.client.ManagerClient;
import ru.nsu.fit.crack.hash.crackhashworker.dto.CrackHashManagerRequest;
import ru.nsu.fit.crack.hash.crackhashworker.dto.CrackHashWorkerResponse;
import ru.nsu.fit.crack.hash.crackhashworker.mapper.TaskMapper;
import ru.nsu.fit.crack.hash.crackhashworker.rabbitmq.producer.ManagerProducer;
import ru.nsu.fit.crack.hash.crackhashworker.task.CrackHashTask;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final ManagerProducer managerProducer;
    private final ManagerClient managerClient;
    private final Map<String, CrackHashTask> tasks = new ConcurrentHashMap<>();

    public Mono<Void> runTask(CrackHashManagerRequest request) {
        return Mono.fromCallable(() -> createTask(request).call())
            .subscribeOn(Schedulers.boundedElastic())
            .map(words -> TaskMapper.mapResponse(request, words))
            .flatMap(managerClient::sendResultManager)
            .doOnError(e -> log.error("Error in runTask pipeline", e))
            .then();
    }

    public Mono<Void> runTask(long tag, Channel channel, CrackHashManagerRequest request) {
        return Mono.fromCallable(() -> createTask(request).call())
            .subscribeOn(Schedulers.boundedElastic())
            .doOnNext(words -> log.info("Mapped words: {}", words))
            .map(words -> TaskMapper.mapResponse(request, words))
            .flatMap(message -> sendMessage(tag, channel, message))
            .doOnError(e -> log.error("Error in runTask pipeline", e))
            .then();
    }

    public int getNumberCalculatedWord(String keyTask) {
        CrackHashTask crackHashTask = tasks.get(keyTask);
        log.debug("getNumberCalculatedWord keyTask: {}, value {}", keyTask, crackHashTask);
        return crackHashTask.numberWordCounter().get();
    }

    private Mono<Void> sendMessage(long tag, Channel channel, CrackHashWorkerResponse response) {
        try {
            managerProducer.send(response);
            channel.basicAck(tag, false);
            return Mono.empty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CrackHashTask createTask(CrackHashManagerRequest request) {
        CrackHashTask task = TaskMapper.mapRequest(request);
        log.debug("createTask request key: {}, task {}", request.getRequestId(), task);
        tasks.put(request.getRequestId(), task);
        return task;
    }
}
