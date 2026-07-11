package br.com.officyna.serviceorder.api.handler;

import br.com.officyna.infrastructure.exception.ErrorResponse;
import br.com.officyna.serviceorder.domain.exception.ServiceOrderBusinessException;
import br.com.officyna.serviceorder.domain.exception.ServiceOrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ServiceOrderExceptionHandler {

    @ExceptionHandler(ServiceOrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ServiceOrderNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(ServiceOrderBusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(ServiceOrderBusinessException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }
}
