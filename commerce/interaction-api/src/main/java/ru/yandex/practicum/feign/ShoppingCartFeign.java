package ru.yandex.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartFeign {

    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam String username);

    @PutMapping
    ShoppingCartDto addProductToCart(@RequestParam String username, @RequestBody Map<UUID, Integer> products);

    @DeleteMapping
    void deactivateShoppingCart(@RequestParam String username);

    @PostMapping("/remove")
    ShoppingCartDto removeProductFromCart(@RequestParam String username, @RequestBody List<UUID> productIds);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeQuantity(@RequestParam String username, @RequestBody ChangeProductQuantityRequest request);
}
