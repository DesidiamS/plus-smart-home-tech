package ru.yandex.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class OrderErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleServerException(RuntimeException e) {
        log.error(Arrays.toString(e.getStackTrace()));
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.name(),
                        e.getMessage()
                ), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleAuthException(NotAuthorizedUserException e) {
        log.error(Arrays.toString(e.getStackTrace()));
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.UNAUTHORIZED.name(),
                        e.getMessage()
                ), HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNoOrderFoundException(NoOrderFoundException e) {
        log.error(Arrays.toString(e.getStackTrace()));

        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.NOT_FOUND.name(),
                        e.getMessage()
                ), HttpStatus.NOT_FOUND
        );
    }
}
