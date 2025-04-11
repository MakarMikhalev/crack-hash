package ru.nsu.fit.crack.hash.crackhashworker.service;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;
import ru.nsu.fit.crack.hash.crackhashworker.client.ManagerClient;
import ru.nsu.fit.crack.hash.crackhashworker.dto.CrackHashManagerRequest;
import ru.nsu.fit.crack.hash.crackhashworker.dto.CrackHashWorkerResponse;
import ru.nsu.fit.crack.hash.crackhashworker.exception.SendException;
import ru.nsu.fit.crack.hash.crackhashworker.mapper.TaskMapper;
import ru.nsu.fit.crack.hash.crackhashworker.rabbitmq.producer.ManagerProducer;
import ru.nsu.fit.crack.hash.crackhashworker.task.CrackHashTask;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final ManagerProducer managerProducer;
    private final ManagerClient managerClient;
    private final Map<String, CrackHashTask> tasks = new ConcurrentHashMap<>();
    private final ConnectionFactory connectionFactory;

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
            .onErrorResume(e -> {
                log.error("Failed to send result, retrying...", e);
                return reconnectAndRetry(tag, request);
            })
            .then();
    }

    public int getNumberCalculatedWord(String keyTask) {
        final int calculateWords = tasks.get(keyTask).numberWordCounter().get();
        log.debug("getNumberCalculatedWord keyTask: {}, value {}", keyTask, calculateWords);
        return calculateWords;
    }

    public Mono<Void> sendMessage(long tag, Channel channel, CrackHashWorkerResponse response) {
        return Mono.fromRunnable(() -> {
                try {
                    managerProducer.send(response);
                    channel.basicAck(tag, false);
                } catch (IOException exception) {
                    throw new SendException(exception);
                }
            })
            .retryWhen(Retry.backoff(30, Duration.ofMillis(500))
                .maxBackoff(Duration.ofSeconds(5))
                .doBeforeRetry(retrySignal ->
                    log.warn("Retrying after failure (attempt {}): {}",
                        retrySignal.totalRetries(),
                        retrySignal.failure().getMessage())))
            .onErrorResume(e -> {
                log.error("All retry attempts failed for sendMessage", e);
                return Mono.empty();
            }).then();
    }

    private Mono<Void> reconnectAndRetry(long tag, CrackHashManagerRequest request) {
        return Mono.fromCallable(() -> connectionFactory.createConnection().createChannel(false))
            .flatMap(newChannel -> runTask(tag, newChannel, request)
                .doFinally(signal -> {
                    try {
                        if (newChannel.isOpen()) newChannel.close();
                    } catch (Exception e) {
                        log.error("Failed to close channel", e);
                    }
                }));
    }

    private CrackHashTask createTask(CrackHashManagerRequest request) {
        CrackHashTask task = TaskMapper.mapRequest(request);
        log.debug("createTask request key: {}, task {}", request.getRequestId(), task);
        tasks.merge(request.getRequestId(), task, (oldV, newV) -> {
            newV.numberWordCounter().addAndGet(oldV.numberWordCounter().get());
            return newV;
        });
        return task;
    }
}
