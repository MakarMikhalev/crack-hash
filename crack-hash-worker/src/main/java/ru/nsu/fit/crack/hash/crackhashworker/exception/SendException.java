package ru.nsu.fit.crack.hash.crackhashworker.exception;

public class SendException extends RuntimeException {
    public SendException(Throwable exception) {
        super("Failed to send message", exception);
    }
}
