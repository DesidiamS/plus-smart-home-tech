package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.domain.WarehouseProduct;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WarehouseMapper {

    WarehouseProduct toWarehouseProduct(NewProductInWarehouseRequest newProductInWarehouseRequest);
}
