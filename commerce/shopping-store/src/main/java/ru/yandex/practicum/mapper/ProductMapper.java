package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.domain.Product;
import ru.yandex.practicum.dto.ProductDto;

import java.util.List;

@Component
public class ProductMapper {

    public ProductDto toProductDto(Product product) {
        return new ProductDto(
                product.getProductId(),
                product.getProductName(),
                product.getDescription(),
                product.getImageSrc(),
                product.getQuantityState(),
                product.getProductState(),
                product.getProductCategory(),
                product.getPrice());
    }

    public Product toProduct(ProductDto productDto) {
        return new Product(
                productDto.getProductId(),
                productDto.getProductName(),
                productDto.getDescription(),
                productDto.getImageSrc(),
                productDto.getPrice(),
                productDto.getQuantityState(),
                productDto.getProductState(),
                productDto.getProductCategory()
        );
    }

    public List<ProductDto> toProductDtoList(List<Product> products) {
        return products.stream()
                .map(product -> new ProductDto(product.getProductId(),
                        product.getProductName(),
                        product.getDescription(),
                        product.getImageSrc(),
                        product.getQuantityState(),
                        product.getProductState(),
                        product.getProductCategory(),
                        product.getPrice())).toList();
    }
}
