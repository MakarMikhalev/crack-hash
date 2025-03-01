package ru.nsu.fit.crack.hash.crackhashworker.task;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import org.paukov.combinatorics3.Generator;
import ru.nsu.fit.crack.hash.crackhashworker.exception.AlgorithmNotFoundException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.Callable;

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
        /* Шаг 1: Подсчёт общего количества возможных слов */
        final int alphabetSize = alphabet.size();

        final int countWord = (int) ((Math.pow(alphabetSize, maxLength + 1) - alphabetSize) / (alphabetSize - 1));
        // Определяем базовый размер части
        final int remainder = countWord % partCount;
        final int partSize = countWord / partCount;
        final int offset = partSize * partNumber + Math.min(partNumber, remainder);
        return Generator.permutation(alphabet)
            .withRepetitions(maxLength)
            .stream()
            .skip(offset)
            .limit(partSize + (partNumber < remainder ? 1 : 0))
            .map(word -> String.join("", word))
            .filter(word -> hash.equals(this.calcHash(String.join("", word))))
            .toList();
    }

    @Nonnull
    private String calcHash(String word) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM_HASH);
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
