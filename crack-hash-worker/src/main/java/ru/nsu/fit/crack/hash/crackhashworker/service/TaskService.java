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

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final ManagerProducer managerProducer;
    private final ManagerClient managerClient;

    public Mono<Void> runTask(CrackHashManagerRequest request) {
        return Mono.fromCallable(() -> TaskMapper.mapRequest(request).call())
            .subscribeOn(Schedulers.boundedElastic())
            .map(words -> TaskMapper.mapResponse(request, words))
            .flatMap(managerClient::sendResultManager)
            .doOnError(e -> log.error("Error in runTask pipeline", e))
            .then();
    }

    public Mono<Void> runTask(long tag, Channel channel, CrackHashManagerRequest request) {
        return Mono.fromCallable(() -> TaskMapper.mapRequest(request).call())
            .subscribeOn(Schedulers.boundedElastic())
            .doOnNext(words -> log.info("Mapped words: {}", words))
            .map(words -> TaskMapper.mapResponse(request, words))
            .flatMap(message -> sendMessage(tag, channel, message))
            .doOnError(e -> log.error("Error in runTask pipeline", e))
            .then();
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
}
