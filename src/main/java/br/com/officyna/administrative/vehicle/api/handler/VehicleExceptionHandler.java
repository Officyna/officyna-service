package br.com.officyna.administrative.vehicle.api.handler;

import br.com.officyna.administrative.vehicle.domain.exception.VehicleBusinessException;
import br.com.officyna.administrative.vehicle.domain.exception.VehicleNotFoundException;
import br.com.officyna.infrastructure.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class VehicleExceptionHandler {

    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(VehicleNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(VehicleBusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(VehicleBusinessException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }
}
