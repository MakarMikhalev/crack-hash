package ru.nsu.fit.crack.hash.crackhashworker.task;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import org.paukov.combinatorics3.Generator;
import ru.nsu.fit.crack.hash.crackhashworker.exception.AlgorithmNotFoundException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Builder
public record CrackHashTask(
    int partNumber,
    int partCount,
    String hash,
    int maxLength,
    List<String> alphabet
) implements Callable<List<String>> {
    private static final String ALGORITHM_HASH = "MD5";

    @Override
    public List<String> call() {
        // Шаг 1: Подсчёт общего количества возможных слов
        final int alphabetSize = alphabet.size();
        final int totalWords = (int) ((Math.pow(alphabetSize, maxLength + 1) - alphabetSize) / (alphabetSize - 1));

        // Шаг 2: Определение диапазона слов для текущего воркера
        final int partSize = totalWords / partCount;
        final int remainder = totalWords % partCount;
        final int offset = partSize * partNumber + Math.min(partNumber, remainder);
        final int limit = partSize + (partNumber < remainder ? 1 : 0);

        // Шаг 3: Генерация и фильтрация слов
        AtomicInteger atomicOffset = new AtomicInteger(offset);
        AtomicInteger atomicLimit = new AtomicInteger(limit);
       return IntStream.rangeClosed(1, maxLength)
            .boxed()
            .filter(v -> atomicLimit.get() > 0)
            .flatMap(length -> {
                final int wordsOfCurrentLength = (int) Math.pow(alphabet.size(), length);
                final int wordsToProcess = Math.min(wordsOfCurrentLength, atomicLimit.get());
                Stream<String> wordsStream = Generator.permutation(alphabet)
                    .withRepetitions(length)
                    .stream()
                    .skip(atomicOffset.get())
                    .limit(wordsToProcess)
                    .map(word -> String.join("", word))
                    .filter(word -> hash.equals(calcHash(word)));
                atomicLimit.addAndGet(-wordsToProcess);
                return wordsStream;
            })
            .limit(atomicLimit.get())
            .toList();
    }

    /**
     * Вычисляет хэш строки с использованием алгоритма MD5.
     */
    @Nonnull
    private String calcHash(String word) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(word.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashInBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new AlgorithmNotFoundException(ALGORITHM_HASH);
        }
    }
}
