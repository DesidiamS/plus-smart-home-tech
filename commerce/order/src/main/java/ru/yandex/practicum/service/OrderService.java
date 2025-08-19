package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    List<OrderDto> getUserOrders(String username);

    OrderDto createOrder(CreateNewOrderRequest request);

    OrderDto returnOrder(ProductReturnRequest request);

    OrderDto payOrder(UUID orderId);

    OrderDto payOrderFailed(UUID orderId);

    OrderDto deliverOrder(UUID orderId);

    OrderDto deliverOrderFailed(UUID orderId);

    OrderDto orderCompleted(UUID orderId);

    OrderDto calculateTotal(UUID orderId);

    OrderDto calculateDelivery(UUID orderId);

    OrderDto orderAssembly(UUID orderId);

    OrderDto orderAssemblyFailed(UUID orderId);
}
