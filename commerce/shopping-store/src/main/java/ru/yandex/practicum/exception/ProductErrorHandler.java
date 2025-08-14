package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
public class ProductErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(RuntimeException e) {
        return new ResponseEntity<>(new ErrorResponse(
                e.getCause(),
                Arrays.asList(e.getStackTrace()),
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                e.getMessage(),
                "Internal Server Error",
                Arrays.asList(e.getSuppressed()),
                e.getLocalizedMessage()), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse(
                e.getCause(),
                Arrays.asList(e.getStackTrace()),
                HttpStatus.NOT_FOUND.name(),
                e.getMessage(),
                "Not Found",
                Arrays.asList(e.getSuppressed()),
                e.getLocalizedMessage()), HttpStatus.NOT_FOUND
        );
    }
}
