package ru.nsu.fit.crack.hash.crackhashworker.controller;

import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.nsu.fit.crack.hash.crackhashworker.client.ManagerClient;
import ru.nsu.fit.crack.hash.crackhashworker.dto.CrackHashManagerRequest;
import ru.nsu.fit.crack.hash.crackhashworker.dto.CrackHashWorkerResponse;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ManagerControllerTest {
    @Autowired private WebTestClient webTestClient;
    @Autowired private ManagerController managerController;
    @MockitoBean private ManagerClient managerClient;
    @Captor ArgumentCaptor<CrackHashWorkerResponse> argumentCaptor;

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final String HASH = "e2fc714c4727ee9395f324cd2e7f331f";
    private static final String REQUEST_ID = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(managerController).build();
    }

    @Test
    void handleTaskTest() throws Exception {
        final CrackHashManagerRequest request = createRequestBody();
        try (var ignore = mockManagerClient()) {
            webTestClient.post()
                .uri("/internal/api/worker/hash/crack/task")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
            verifyResult();
        }
    }

    private void verifyResult() {
        CrackHashWorkerResponse capturedResponse = argumentCaptor.getValue();
        Assertions.assertNotNull(capturedResponse);
        Assertions.assertEquals(REQUEST_ID, capturedResponse.getRequestId());
        Assertions.assertEquals(0, capturedResponse.getPartNumber());
        Assertions.assertNotNull(capturedResponse.getAnswers());
        Assertions.assertFalse(capturedResponse.getAnswers().getWords().isEmpty());
        Assertions.assertEquals("abcd", capturedResponse.getAnswers().getWords().get(0));
    }

    @Nonnull
    private CrackHashManagerRequest createRequestBody() {
        final CrackHashManagerRequest request = new CrackHashManagerRequest();
        request.setRequestId(REQUEST_ID);
        request.setHash(HASH);
        var alphabet = new CrackHashManagerRequest.Alphabet();
        alphabet.getSymbols().addAll(ALPHABET.chars().mapToObj(c -> String.valueOf((char) c)).toList());
        request.setAlphabet(alphabet);
        request.setMaxLength(4);
        request.setPartCount(4);
        request.setPartNumber(0);
        return request;
    }

    @Nonnull
    private AutoCloseable mockManagerClient() {
        Mockito.when(managerClient.sendResultManager(argumentCaptor.capture())).thenReturn(Mono.empty());
        return () -> Mockito.verify(managerClient).sendResultManager(Mockito.any(CrackHashWorkerResponse.class));
    }
}
