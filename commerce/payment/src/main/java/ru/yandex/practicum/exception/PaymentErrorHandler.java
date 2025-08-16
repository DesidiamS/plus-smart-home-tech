package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
public class PaymentErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleServerException(RuntimeException e) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        e.getCause(),
                        Arrays.asList(e.getStackTrace()),
                        HttpStatus.BAD_REQUEST.name(),
                        e.getMessage(),
                        "Недостаточно информации для расчёта стоимости заказа",
                        Arrays.asList(e.getSuppressed()),
                        e.getLocalizedMessage()
                ), HttpStatus.BAD_REQUEST
        );
    }
}
