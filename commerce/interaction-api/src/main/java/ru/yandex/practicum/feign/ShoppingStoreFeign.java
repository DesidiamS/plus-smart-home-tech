package ru.yandex.practicum.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.PageableDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.model.ProductCategory;
import ru.yandex.practicum.model.QuantityState;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreFeign {

    @GetMapping
    List<ProductDto> getProducts(@RequestParam ProductCategory category, @RequestParam PageableDto pageableDto);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable String productId);

    @PostMapping
    ProductDto createProduct(@Valid @RequestBody ProductDto productDto);

    @PutMapping
    ProductDto updateProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    boolean removeProductFromStore(@RequestBody String productId);

    @PostMapping("/quantityState")
    boolean updateQuantityState(@RequestParam UUID productId, @RequestParam QuantityState quantityState);
}
