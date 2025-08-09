package ru.yandex.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseFeign {

    @PutMapping
    void addNewProductToWarehouse(@RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/check")
    BookedProductsDto checkShoppingCart(@RequestBody ShoppingCartDto shoppingCartDto);

    @PostMapping("/add")
    void addProductToWarehouse(@RequestBody AddProductToWarehouseRequest request);

    @GetMapping("/address")
    AddressDto checkAddress();

    @PostMapping("/product")
    BookedProductsDto buyProduct(@RequestBody ShoppingCartDto shoppingCart);
}
