package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.domain.WarehouseProduct;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.repository.WarehouseProductRepository;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.yandex.practicum.mapper.WarehouseMapper.toWarehouseProduct;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseProductRepository repository;

    @Override
    public void addNewProduct(NewProductInWarehouseRequest request) {
        if (repository.findById(request.getProductId()).isPresent()) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    "Такой товар уже существует. ID: " + request.getProductId()
            );
        }

        repository.save(toWarehouseProduct(request));
    }

    @Override
    public BookedProductsDto checkProducts(ShoppingCartDto shoppingCart) {
        BookedProductsDto bookedProducts = new BookedProductsDto(
                0.0,
                0.0,
                false
        );
        for (Map.Entry<UUID, Integer> productEntry : shoppingCart.getProducts().entrySet()) {
            WarehouseProduct warehouseProduct = repository.findById(productEntry.getKey()).orElseThrow(() ->
                    new NoSpecifiedProductInWarehouseException("Такого товара нет на складе"));

            if (warehouseProduct.getQuantity() < productEntry.getValue()) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("Не хватает количества товара на складе. ID: " + productEntry.getKey());
            }

            if (warehouseProduct.getFragile()) {
                bookedProducts.setFragile(true);
            }

            bookedProducts.setDeliveryWeight(bookedProducts.getDeliveryWeight() + warehouseProduct.getWeight());
            Double volume = warehouseProduct.getHeight() * warehouseProduct.getWeight() * warehouseProduct.getDepth();
            bookedProducts.setDeliveryVolume(bookedProducts.getDeliveryVolume() + volume);
        }

        return bookedProducts;
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct warehouseProduct = repository.findByProductId(request.getProductId()).orElseThrow(() ->
                new NoSpecifiedProductInWarehouseException("Такого товара нет на складе"));

        warehouseProduct.setQuantity(warehouseProduct.getQuantity() + request.getQuantity());

        repository.save(warehouseProduct);
    }

    @Override
    public AddressDto checkAddress() {
        return new AddressDto(
                Address.CURRENT_ADDRESS,
                Address.CURRENT_ADDRESS,
                Address.CURRENT_ADDRESS,
                Address.CURRENT_ADDRESS,
                Address.CURRENT_ADDRESS
        );
    }

    @Override
    @Transactional
    public BookedProductsDto buyProducts(ShoppingCartDto shoppingCart) {
        BookedProductsDto bookedProducts = checkProducts(shoppingCart);

        List<WarehouseProduct> products = new ArrayList<>();
        for (Map.Entry<UUID, Integer> productEntry : shoppingCart.getProducts().entrySet()) {
            WarehouseProduct product = repository.findByProductId(productEntry.getKey()).orElseThrow(() ->
                    new NoSpecifiedProductInWarehouseException("Такого товара нет на складе!"));
            product.setQuantity(product.getQuantity() - productEntry.getValue());
            products.add(product);
        }

        repository.saveAll(products);

        return bookedProducts;
    }
}
