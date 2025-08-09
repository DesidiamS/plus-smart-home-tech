package ru.yandex.practicum.exception;

public class NoProductsInShoppingException extends RuntimeException {
    public NoProductsInShoppingException(String message) {
        super(message);
    }
}
