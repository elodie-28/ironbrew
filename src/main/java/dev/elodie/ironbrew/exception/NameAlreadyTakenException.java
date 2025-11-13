package dev.elodie.ironbrew.exception;

public class NameAlreadyTakenException extends RuntimeException {
    public NameAlreadyTakenException() {
        super();
    }
    public NameAlreadyTakenException(String message) {
        super(message);
    }
}
