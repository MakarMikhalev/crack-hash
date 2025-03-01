package ru.nsu.fit.crack.hash.crackhashworker.exception;

public class AlgorithmNotFoundException extends RuntimeException {
    public AlgorithmNotFoundException(String algorithmName) {
        super("Algorithm %s not found".formatted(algorithmName));
    }
}
