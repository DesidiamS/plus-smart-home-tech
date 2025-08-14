package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
public class WarehouseErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNoSpecifiedProductInWarehouse(NoSpecifiedProductInWarehouseException e) {
        return new ResponseEntity<>(new ErrorResponse(
                e.getCause(),
                Arrays.asList(e.getStackTrace()),
                HttpStatus.BAD_REQUEST.name(),
                e.getMessage(),
                "Product not found in warehouse",
                Arrays.asList(e.getSuppressed()),
                e.getLocalizedMessage()
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleProductInShoppingCartLowQuantity(ProductInShoppingCartLowQuantityInWarehouse e) {
        return new ResponseEntity<>(new ErrorResponse(
                e.getCause(),
                Arrays.asList(e.getStackTrace()),
                HttpStatus.BAD_REQUEST.name(),
                e.getMessage(),
                "Product low quantity",
                Arrays.asList(e.getSuppressed()),
                e.getLocalizedMessage()
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleSpecifiedProductAlreadyInWarehouse(SpecifiedProductAlreadyInWarehouseException e) {
        return new ResponseEntity<>(new ErrorResponse(
                e.getCause(),
                Arrays.asList(e.getStackTrace()),
                HttpStatus.BAD_REQUEST.name(),
                e.getMessage(),
                "Product already exists",
                Arrays.asList(e.getSuppressed()),
                e.getLocalizedMessage()
        ), HttpStatus.BAD_REQUEST);
    }
}
