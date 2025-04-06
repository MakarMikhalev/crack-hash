package ru.nsu.fit.crack.hash.crackhashmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.fit.crack.hash.crackhashmanager.configuration.properties.ApplicationProperties;
import ru.nsu.fit.crack.hash.crackhashmanager.mapper.CrackHashMapper;
import ru.nsu.fit.crack.hash.crackhashmanager.model.Task;
import ru.nsu.fit.crack.hash.crackhashmanager.rabbitmq.producer.WorkerProducer;

import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DistributionService {
    private final ApplicationProperties applicationProperties;
    private final CrackHashMapper crackHashMapper;
    private final WorkerProducer workerProducer;

    /**
     * Распределение задачи по воркерам для расчета хэша.
     *
     * <p>Задача делится на несколько частей, которые отправляются разным воркерам.
     *
     * @param task Запрос на обработку хэша, содержащий параметры (хэш, алфавит, макс. длину).
     */
    public void distribution(Task task) {
        IntStream.range(0, applicationProperties.numberWorkers())
                .forEach(partNumber -> workerProducer.send(crackHashMapper.mapToDto(task.getId(), partNumber, task)));
    }
}
