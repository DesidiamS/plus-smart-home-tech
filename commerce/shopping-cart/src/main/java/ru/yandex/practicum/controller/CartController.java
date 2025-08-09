package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.service.CartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String username) {
        return cartService.getShoppingCartByUsername(username);
    }

    @PutMapping
    public ShoppingCartDto addProductToCart(@RequestParam String username, @RequestBody Map<UUID, Integer> products) {
        return cartService.addToCart(username, products);
    }

    @DeleteMapping
    public void deactivateShoppingCart(@RequestParam String username) {
        cartService.deactivateCart(username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeProductFromCart(@RequestParam String username, @RequestBody List<UUID> productIds) {
        return cartService.removeFromCart(username, productIds);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantity(@RequestParam String username, @RequestBody ChangeProductQuantityRequest request) {
        return cartService.changeQuantity(username, request);
    }
}
