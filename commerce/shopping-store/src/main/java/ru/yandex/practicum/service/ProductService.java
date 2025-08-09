package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.PageableDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.model.ProductCategory;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;

import java.util.List;

public interface ProductService {

    List<ProductDto> getProducts(ProductCategory category, PageableDto pageableDto);

    ProductDto getProduct(String productId);

    ProductDto createProduct(ProductDto request);

    ProductDto updateProduct(ProductDto request);

    boolean removeProductFromStore(String productId);

    boolean updateQuantityState(SetProductQuantityStateRequest request);

}
