package br.com.officyna.infrastructure.exception;

public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}