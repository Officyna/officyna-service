package br.com.officyna.administrative.customer.domain.exception;

public class CustomerBusinessException extends RuntimeException {

    public CustomerBusinessException(String message) {
        super(message);
    }
}
