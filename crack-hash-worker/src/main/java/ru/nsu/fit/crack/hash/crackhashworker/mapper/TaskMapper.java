package ru.nsu.fit.crack.hash.crackhashworker.mapper;

import jakarta.annotation.Nonnull;
import lombok.experimental.UtilityClass;
import ru.nsu.fit.crack.hash.crackhashworker.dto.CrackHashManagerRequest;
import ru.nsu.fit.crack.hash.crackhashworker.dto.CrackHashWorkerResponse;
import ru.nsu.fit.crack.hash.crackhashworker.task.CrackHashTask;

import java.util.List;
import java.util.UUID;

@UtilityClass
public class TaskMapper {
    @Nonnull
    public CrackHashTask mapRequest(CrackHashManagerRequest request) {
        return CrackHashTask.builder()
            .partNumber(request.getPartNumber())
            .partCount(request.getPartCount())
            .alphabet(request.getAlphabet().getSymbols())
            .maxLength(request.getMaxLength())
            .hash(request.getHash())
            .build();
    }

    @Nonnull
    public CrackHashWorkerResponse mapResponse(CrackHashManagerRequest request, List<String> words) {
        CrackHashWorkerResponse response = new CrackHashWorkerResponse();
        response.setRequestId(request.getRequestId());
        response.setPartNumber(request.getPartNumber());
        var answer = new CrackHashWorkerResponse.Answers();
        answer.getWords().addAll(words);
        response.setAnswers(answer);
        return response;
    }
}
