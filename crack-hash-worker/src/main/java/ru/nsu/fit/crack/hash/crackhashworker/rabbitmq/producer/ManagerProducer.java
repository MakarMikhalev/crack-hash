package ru.nsu.fit.crack.hash.crackhashworker.rabbitmq.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.nsu.fit.crack.hash.crackhashworker.configuration.properties.ApplicationProperties;
import ru.nsu.fit.crack.hash.crackhashworker.dto.CrackHashWorkerResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class ManagerProducer {
    private final RabbitTemplate rabbitTemplate;
    private final ApplicationProperties applicationProperties;

    public void send(CrackHashWorkerResponse response) {
        log.info("Sending worker request: {}", response);
        rabbitTemplate.convertAndSend(
            applicationProperties.exchangeName(),
            applicationProperties.exchangeKey(),
            response);
    }
}
