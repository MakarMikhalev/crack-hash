package ru.nsu.fit.crack.hash.crackhashmanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.nsu.fit.crack.hash.crackhashmanager.client.WorkerClient;
import ru.nsu.fit.crack.hash.crackhashmanager.controller.ClientController;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.client.HashRequestDto;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "application.number-workers=4"
)
class ClientControllerTest {
    @Autowired private WebTestClient webTestClient;
    @Autowired private ClientController clientController;
    @MockitoBean WorkerClient workerClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(clientController).build();
    }

    @Test
    void testSendRequest() throws Exception {
        final HashRequestDto request = new HashRequestDto(
            "e2fc714c4727ee9395f324cd2e7f331f",
            4
        );
        try (var ignored = mockWorkClient()) {
            WebTestClient.ResponseSpec responseSpec = webTestClient.post()
                .uri("/api/hash/crack")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
            Assertions.assertNotNull(responseSpec.expectBody());
        }
    }

    private AutoCloseable mockWorkClient() {
        Mockito.doNothing().when(workerClient).send(Mockito.any(), Mockito.any());
        return () ->
            Mockito.verify(workerClient, Mockito.times(4)).send(Mockito.any(), Mockito.any());
    }
}
