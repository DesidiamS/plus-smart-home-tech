package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.domain.WarehouseProduct;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

@Component
public class WarehouseMapper {

    public WarehouseProduct toWarehouseProduct(NewProductInWarehouseRequest newProductInWarehouseRequest) {
        return new WarehouseProduct(
                null,
                newProductInWarehouseRequest.getProductId(),
                0,
                newProductInWarehouseRequest.getFragile(),
                newProductInWarehouseRequest.getDimension().getWidth(),
                newProductInWarehouseRequest.getDimension().getHeight(),
                newProductInWarehouseRequest.getDimension().getDepth(),
                newProductInWarehouseRequest.getWeight());
    }
}
