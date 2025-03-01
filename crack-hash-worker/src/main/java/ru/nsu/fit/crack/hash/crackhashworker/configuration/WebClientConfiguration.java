package ru.nsu.fit.crack.hash.crackhashworker.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;
import ru.nsu.fit.crack.hash.crackhashworker.configuration.properties.ApplicationProperties;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {
    private final ApplicationProperties applicationProperties;

    public static final int TIMEOUT = 60;

    @Bean
    public WebClient webClientWithTimeout() {
        log.info("URL MANAGER {}", applicationProperties.domainManager());
        final var tcpClient = TcpClient
            .create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(TIMEOUT, TimeUnit.SECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(TIMEOUT, TimeUnit.SECONDS));
            });

        return WebClient.builder()
            .baseUrl(applicationProperties.domainManager())
            .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
            .build();
    }
}
