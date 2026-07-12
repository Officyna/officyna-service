package br.com.officyna.administrative.supply.api.handler;

import br.com.officyna.administrative.supply.domain.exception.SupplyBusinessException;
import br.com.officyna.administrative.supply.domain.exception.SupplyNotFoundException;
import br.com.officyna.infrastructure.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SupplyExceptionHandler {

    @ExceptionHandler(SupplyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(SupplyNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(SupplyBusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(SupplyBusinessException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }
}
