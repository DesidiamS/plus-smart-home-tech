package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.domain.WarehouseProduct;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

@Mapper
public class WarehouseMapper {

    public static WarehouseProduct toWarehouseProduct(NewProductInWarehouseRequest request) {
        return new WarehouseProduct(
                null,
                request.getProductId(),
                0,
                request.getFragile(),
                request.getDimension().getWidth(),
                request.getDimension().getHeight(),
                request.getDimension().getDepth(),
                request.getWeight()
        );
    }
}
