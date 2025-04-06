package ru.nsu.fit.crack.hash.crackhashworker.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MarshallingMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import ru.nsu.fit.crack.hash.crackhashworker.dto.CrackHashManagerRequest;
import ru.nsu.fit.crack.hash.crackhashworker.dto.CrackHashWorkerResponse;

@Configuration
@RequiredArgsConstructor
public class RabbitMqConfiguration {

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setSupportJaxbElementClass(true);
        marshaller.setClassesToBeBound(CrackHashWorkerResponse.class, CrackHashManagerRequest.class);
        return marshaller;
    }

    @Bean
    public MarshallingMessageConverter messageConverter(Jaxb2Marshaller jaxb2Marshaller) {
        var messageConverter = new MarshallingMessageConverter(jaxb2Marshaller, jaxb2Marshaller);
        messageConverter.setContentType("application/xml");
        return messageConverter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
        ConnectionFactory connectionFactory,
        MarshallingMessageConverter marshallingMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(marshallingMessageConverter);
        return rabbitTemplate;
    }
}
