package ru.yandex.practicum.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.DimensionDto;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewProductInWarehouseRequest {

    UUID productId;
    Boolean fragile;
    DimensionDto dimension;
    Double weight;
}
