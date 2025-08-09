package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.domain.Product;
import ru.yandex.practicum.dto.ProductDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    ProductDto toProductDto(Product product);

    Product toProduct(ProductDto productDto);

    List<ProductDto> toProductDtoList(List<Product> products);
}
