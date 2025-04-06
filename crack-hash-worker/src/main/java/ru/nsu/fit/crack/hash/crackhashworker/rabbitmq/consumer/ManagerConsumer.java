package ru.nsu.fit.crack.hash.crackhashworker.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import ru.nsu.fit.crack.hash.crackhashworker.dto.CrackHashManagerRequest;
import ru.nsu.fit.crack.hash.crackhashworker.service.TaskService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ManagerConsumer {
    private final TaskService taskService;

    @RabbitListener(queues = "${application.queue-name-consumer}")
    public void listen(@Header(AmqpHeaders.DELIVERY_TAG) long tag, Channel channel, CrackHashManagerRequest crackHashManagerRequest) {
        log.info("Received message {}", crackHashManagerRequest.getRequestId());
        taskService.runTask(tag, channel, crackHashManagerRequest).block();
    }
}
