package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.PageableDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.model.ProductCategory;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    List<ProductDto> getProducts(ProductCategory category, PageableDto pageableDto);

    ProductDto getProduct(UUID productId);

    ProductDto createProduct(ProductDto request);

    ProductDto updateProduct(ProductDto request);

    boolean removeProductFromStore(UUID productId);

    boolean updateQuantityState(SetProductQuantityStateRequest request);

}
