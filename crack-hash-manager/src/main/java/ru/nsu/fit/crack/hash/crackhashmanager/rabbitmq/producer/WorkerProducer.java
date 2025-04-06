package ru.nsu.fit.crack.hash.crackhashmanager.rabbitmq.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ru.nsu.fit.crack.hash.crackhashmanager.configuration.properties.ApplicationProperties;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.worker.CrackHashManagerRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkerProducer {
    private final RabbitTemplate rabbitTemplate;
    private final ApplicationProperties applicationProperties;

    public void send(CrackHashManagerRequest request) {
        log.info("Sending worker request: {}", request);

        rabbitTemplate.convertAndSend(
            applicationProperties.exchangeName(),
            applicationProperties.exchangeKeyProducer(),
            request);

        log.info("After sending worker request: {}", request);
    }
}
