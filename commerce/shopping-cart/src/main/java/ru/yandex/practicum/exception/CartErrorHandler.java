package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
public class CartErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleServerException(RuntimeException e) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        e.getCause(),
                        Arrays.asList(e.getStackTrace()),
                        HttpStatus.INTERNAL_SERVER_ERROR.name(),
                        e.getMessage(),
                        "Internal Server Error",
                        Arrays.asList(e.getSuppressed()),
                        e.getLocalizedMessage()
                ), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleAuthException(NotAuthorizedUserException e) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        e.getCause(),
                        Arrays.asList(e.getStackTrace()),
                        HttpStatus.UNAUTHORIZED.name(),
                        e.getMessage(),
                        "Not Authorized",
                        Arrays.asList(e.getSuppressed()),
                        e.getLocalizedMessage()
                ), HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNoProductFoundException(NoProductsInShoppingException e) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        e.getCause(),
                        Arrays.asList(e.getStackTrace()),
                        HttpStatus.NOT_FOUND.name(),
                        e.getMessage(),
                        "No Found",
                        Arrays.asList(e.getSuppressed()),
                        e.getLocalizedMessage()
                ), HttpStatus.NOT_FOUND
        );
    }
}
