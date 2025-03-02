package ru.nsu.fit.crack.hash.crackhashmanager.mapper;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.nsu.fit.crack.hash.crackhashmanager.configuration.properties.ApplicationProperties;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.worker.CrackHashManagerRequest;
import ru.nsu.fit.crack.hash.crackhashmanager.model.Task;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CrackHashMapper {
    private final ApplicationProperties applicationProperties;

    @Nonnull
    public CrackHashManagerRequest mapToDto(UUID requestId, int partNumber, Task task) {
        final CrackHashManagerRequest crackHashManagerRequest = new CrackHashManagerRequest();
        crackHashManagerRequest.setRequestId(requestId.toString());
        crackHashManagerRequest.setPartNumber(partNumber);
        crackHashManagerRequest.setPartCount(applicationProperties.numberWorkers());
        crackHashManagerRequest.setHash(task.getHash());
        crackHashManagerRequest.setMaxLength(task.getMaxLength());
        var alphabet = new CrackHashManagerRequest.Alphabet();
        alphabet.getSymbols().addAll(
            applicationProperties.alphabet()
                .chars()
                .mapToObj(c -> String.valueOf((char) c))
                .toList()
        );
        crackHashManagerRequest.setAlphabet(alphabet);
        return crackHashManagerRequest;
    }
}
