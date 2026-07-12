package br.com.officyna.administrative.supply.domain.exception;

public class SupplyNotFoundException extends RuntimeException {

    public SupplyNotFoundException(String message) {
        super(message);
    }

    public static SupplyNotFoundException of(Object id) {
        return new SupplyNotFoundException("Supply not found with id: " + id);
    }
}
