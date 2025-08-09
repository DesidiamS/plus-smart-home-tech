package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.model.ProductCategory;
import ru.yandex.practicum.model.ProductState;
import ru.yandex.practicum.model.QuantityState;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ProductDto {

    @NotBlank
    UUID productId;

    @NotBlank
    String productName;

    @NotBlank
    String description;

    String imageSrc;

    @NotBlank
    QuantityState quantityState;

    @NotBlank
    ProductState productState;

    @NotBlank
    ProductCategory productCategory;

    @NotNull
    Double price;
}
