package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
public class DeliveryErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNoFoundException(RuntimeException e) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        e.getCause(),
                        Arrays.asList(e.getStackTrace()),
                        HttpStatus.NOT_FOUND.name(),
                        e.getMessage(),
                        "Информация о доставке не найдена!",
                        Arrays.asList(e.getSuppressed()),
                        e.getLocalizedMessage()
                ), HttpStatus.NOT_FOUND
        );
    }
}
