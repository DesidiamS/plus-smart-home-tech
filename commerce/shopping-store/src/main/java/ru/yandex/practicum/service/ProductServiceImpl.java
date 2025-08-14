package ru.yandex.practicum.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.domain.Product;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SortDto;
import ru.yandex.practicum.dto.TestDto;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.ProductCategory;
import ru.yandex.practicum.model.ProductState;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    @Override
    public TestDto getProducts(ProductCategory category, Pageable pageable) {

        List<Product> products = productRepository.getProductsByProductCategory(category, pageable);

        if (products.isEmpty()) {
            return null;
        } else {
            List<ProductDto> productList = mapper.toProductDtoList(products);
            List<SortDto> sortDtoList = new LinkedList<>();
            for (Sort.Order order : pageable.getSort()) {
                String property = order.getProperty();
                String direction = order.getDirection().name();
                sortDtoList.add(new SortDto(direction, property));
            }
            return new TestDto(productList, sortDtoList);
        }
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ProductNotFoundException(String.format("Ошибка, товар по id %s в БД не найден", productId)));

        return mapper.toProductDto(product);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto request) {
        Product oldProduct = productRepository.findById(request.getProductId()).orElseThrow(() ->
                new ProductNotFoundException(String.valueOf(request.getProductId())));
        Product newProduct = mapper.toProduct(request);

        newProduct.setProductId(oldProduct.getProductId());
        return mapper.toProductDto(productRepository.save(newProduct));
    }

    @Override
    @Transactional
    public ProductDto addProduct(ProductDto request) {
        Product product = mapper.toProduct(request);
        return mapper.toProductDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public boolean removeProductFromStore(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ProductNotFoundException(String.valueOf(productId)));

        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);

        return true;
    }

    @Override
    @Transactional
    public boolean updateQuantityState(SetProductQuantityStateRequest request) {
        Product product = productRepository.findById(request.getProductId()).orElseThrow(() ->
                new ProductNotFoundException(String.valueOf(request.getProductId())));

        product.setQuantityState(request.getQuantityState());
        productRepository.save(product);

        return true;
    }
}
