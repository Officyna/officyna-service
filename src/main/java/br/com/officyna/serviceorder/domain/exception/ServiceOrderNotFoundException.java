package br.com.officyna.serviceorder.domain.exception;

public class ServiceOrderNotFoundException extends RuntimeException {

    public ServiceOrderNotFoundException(String message) {
        super(message);
    }

    public static ServiceOrderNotFoundException of(Object id) {
        return new ServiceOrderNotFoundException("Service Order not found with id: " + id);
    }
}
