package ru.yandex.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.TestDto;
import ru.yandex.practicum.model.ProductCategory;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;

import java.util.UUID;

public interface ProductService {

    TestDto getProducts(ProductCategory category, Pageable pageable);

    ProductDto getProduct(UUID productId);

    ProductDto updateProduct(ProductDto request);

    ProductDto addProduct(ProductDto request);

    boolean removeProductFromStore(UUID productId);

    boolean updateQuantityState(SetProductQuantityStateRequest request);

}
