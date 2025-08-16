package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.domain.WarehouseDelivery;
import ru.yandex.practicum.domain.WarehouseOrder;
import ru.yandex.practicum.domain.WarehouseProduct;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.repository.WarehouseDeliveryRepository;
import ru.yandex.practicum.repository.WarehouseOrderRepository;
import ru.yandex.practicum.repository.WarehouseProductRepository;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.request.ShippedToDeliveryRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseProductRepository repository;
    private final WarehouseMapper mapper;
    private final WarehouseDeliveryRepository warehouseDeliveryRepository;
    private final WarehouseOrderRepository warehouseOrderRepository;

    @Override
    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest request) {
        if (repository.findById(request.getProductId()).isPresent()) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    "Такой товар уже существует. ID: " + request.getProductId()
            );
        }

        WarehouseProduct warehouseProduct = mapper.toWarehouseProduct(request);

        repository.save(warehouseProduct);
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
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct warehouseProduct = repository.findByProductId(request.getProductId()).orElseThrow(() ->
                new NoSpecifiedProductInWarehouseException("Такого товара нет на складе"));

        warehouseProduct.setQuantity(warehouseProduct.getQuantity() == null ? 0 : warehouseProduct.getQuantity()
                + request.getQuantity());

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

    @Override
    @Transactional
    public void shippedProducts(ShippedToDeliveryRequest request) {
     WarehouseDelivery delivery = warehouseDeliveryRepository.findByOrderId(request.getOrderId())
             .orElseThrow(() -> new NoOrderFoundException("Заказ не найден на складе!"));


     delivery.setDeliveryId(request.getDeliveryId());

     warehouseDeliveryRepository.save(delivery);
    }

    @Override
    public void returnProducts(Map<UUID, Integer> products) {
        List<WarehouseProduct> warehouseProducts = repository.findAllById(products.keySet());

        if (warehouseProducts.isEmpty()) {
            return;
        }

        warehouseProducts.forEach(warehouseProduct -> {
            warehouseProduct.setQuantity(warehouseProduct.getQuantity() + products.get(warehouseProduct.getId()));
            repository.save(warehouseProduct);
        });
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request) {
        BookedProductsDto bookedProducts = checkProducts(new ShoppingCartDto(request.getOrderId(), request.getProducts()));
        WarehouseDelivery delivery = new WarehouseDelivery(
                null,
                request.getOrderId(),
                null
        );

        List<WarehouseOrder> orders = new ArrayList<>();

        for (Map.Entry<UUID, Integer> productEntry : request.getProducts().entrySet()) {
            orders.add(new WarehouseOrder(
                    null,
                    request.getOrderId(),
                    productEntry.getKey(),
                    productEntry.getValue()
            ));
        }

        warehouseDeliveryRepository.save(delivery);
        warehouseOrderRepository.saveAll(orders);

        return bookedProducts;
    }
}