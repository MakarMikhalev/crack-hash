package ru.nsu.fit.crack.hash.crackhashmanager.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.nsu.fit.crack.hash.crackhashmanager.client.WorkerClient;
import ru.nsu.fit.crack.hash.crackhashmanager.configuration.properties.ApplicationProperties;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.client.HashRequestDto;
import ru.nsu.fit.crack.hash.crackhashmanager.dto.client.IdResponseDto;
import ru.nsu.fit.crack.hash.crackhashmanager.mapper.CrackHashMapper;

@Service
@RequiredArgsConstructor
public class DistributionService {
    private final ApplicationProperties applicationProperties;
    private final CrackHashMapper crackHashMapper;
    private final TaskService taskService;
    private final WorkerClient workerClient;

    /**
     * Распределение задачи по воркерам для расчета хэша.
     *
     * <p>Задача делится на несколько частей, которые отправляются разным воркерам.
     * Количество комбинаций рассчитывается по формуле геометрической прогрессии.
     * Затем определяется базовый размер части и остаток, который распределяется между первыми воркерами.</p>
     *
     * @param hashRequestDto Запрос на обработку хэша, содержащий параметры (хэш, алфавит, макс. длину).
     * @return Идентификатор созданной задачи.
     */
    @Nonnull
    public Mono<IdResponseDto> distribution(HashRequestDto hashRequestDto) {
        return taskService.createTask(hashRequestDto.hash())
            .flatMap(task -> Flux.range(0, applicationProperties.numberWorkers())
                .map(partNumber -> crackHashMapper.mapToDto(task.getId(), partNumber, hashRequestDto))
                .flatMap(workerClient::send)
                .then(Mono.just(task)))
            .map(task -> IdResponseDto.builder()
                .requestId(task.getId())
                .build());
    }
}
