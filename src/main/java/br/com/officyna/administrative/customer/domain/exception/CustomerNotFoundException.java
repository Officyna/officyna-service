package br.com.officyna.administrative.customer.domain.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String message) {
        super(message);
    }

    public static CustomerNotFoundException of(Object id) {
        return new CustomerNotFoundException("Customer not found with id: " + id);
    }
}
