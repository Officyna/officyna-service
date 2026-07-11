package br.com.officyna.administrative.labor.api.handler;

import br.com.officyna.administrative.labor.domain.exception.LaborBusinessException;
import br.com.officyna.administrative.labor.domain.exception.LaborNotFoundException;
import br.com.officyna.infrastructure.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LaborExceptionHandler {

    @ExceptionHandler(LaborNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(LaborNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(LaborBusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(LaborBusinessException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }
}
