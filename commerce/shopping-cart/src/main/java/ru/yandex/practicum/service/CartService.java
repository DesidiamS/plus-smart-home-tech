package ru.yandex.practicum.service;

import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CartService {

    ShoppingCartDto getShoppingCartByUsername(String username);

    ShoppingCartDto addToCart(String username, Map<UUID, Integer> items);

    void deactivateCart(String username);

    ShoppingCartDto removeFromCart(String username, List<UUID> items);

    ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest items);

    BookedProductsDto buyProductsInCart(@RequestParam String username);
}
