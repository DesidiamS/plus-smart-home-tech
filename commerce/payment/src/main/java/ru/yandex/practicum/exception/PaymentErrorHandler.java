package ru.yandex.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class PaymentErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleServerException(RuntimeException e) {
        log.error(Arrays.toString(e.getStackTrace()));
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.name(),
                        e.getMessage()
                ), HttpStatus.BAD_REQUEST
        );
    }
}
