package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.domain.Product;
import ru.yandex.practicum.dto.PageableDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.ProductCategory;
import ru.yandex.practicum.model.ProductState;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    @Override
    public List<ProductDto> getProducts(ProductCategory category, PageableDto pageableDto) {
        Pageable pageable = PageRequest.of(pageableDto.getPage(), pageableDto.getSize(),
                Sort.by(Sort.DEFAULT_DIRECTION, String.join(",", pageableDto.getSort())));

        List<Product> products = productRepository.getProductsByProductCategory(category,
                pageable);

        if (products.isEmpty()) {
            return Collections.emptyList();
        } else {
            return mapper.toProductDtoList(products);
        }
    }

    @Override
    public ProductDto getProduct(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ProductNotFoundException(productId));

        return mapper.toProductDto(product);
    }

    @Override
    public ProductDto createProduct(ProductDto request) {
        Product product = mapper.toProduct(request);

        return mapper.toProductDto(productRepository.save(product));
    }

    @Override
    public ProductDto updateProduct(ProductDto request) {
        Product oldProduct = productRepository.findById(request.getProductId()).orElseThrow(() ->
                new ProductNotFoundException(request.getProductId()));
        Product newProduct = mapper.toProduct(request);

        newProduct.setId(oldProduct.getId());
        newProduct.setRating(oldProduct.getRating());
        return mapper.toProductDto(productRepository.save(newProduct));
    }

    @Override
    public boolean removeProductFromStore(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ProductNotFoundException(productId));

        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);

        return true;
    }

    @Override
    public boolean updateQuantityState(SetProductQuantityStateRequest request) {
        Product product = productRepository.findById(request.getProductId()).orElseThrow(() ->
                new ProductNotFoundException(request.getProductId()));

        product.setQuantityState(request.getQuantityState());
        productRepository.save(product);

        return true;
    }
}
