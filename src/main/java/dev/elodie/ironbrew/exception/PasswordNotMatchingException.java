package dev.elodie.ironbrew.exception;

public class PasswordNotMatchingException extends RuntimeException {
    public PasswordNotMatchingException(String message) {
        super(message);
    }
    public PasswordNotMatchingException() {
        super();
    }
}
