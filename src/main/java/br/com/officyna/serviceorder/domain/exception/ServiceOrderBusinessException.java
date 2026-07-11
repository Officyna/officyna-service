package br.com.officyna.serviceorder.domain.exception;

public class ServiceOrderBusinessException extends RuntimeException {

    public ServiceOrderBusinessException(String message) {
        super(message);
    }
}
