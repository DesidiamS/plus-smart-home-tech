package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getUserOrders(@RequestParam String username) {
        return orderService.getUserOrders(username);
    }

    @PutMapping
    public OrderDto createOrder(@RequestBody CreateNewOrderRequest request) {
        return orderService.createOrder(request);
    }

    @PostMapping("/return")
    public OrderDto returnOrder(@RequestBody ProductReturnRequest request) {
        return orderService.returnOrder(request);
    }

    @PostMapping("/payment")
    public OrderDto payOrder(@RequestBody UUID orderId) {
        return orderService.payOrder(orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto payOrderFailed(@RequestBody UUID orderId) {
        return orderService.payOrderFailed(orderId);
    }

    @PostMapping("/delivery")
    public OrderDto deliveryOrder(@RequestBody UUID orderId) {
        return orderService.deliverOrder(orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto deliveryOrderFailed(@RequestBody UUID orderId) {
        return orderService.deliverOrderFailed(orderId);
    }

    @PostMapping("/completed")
    public OrderDto orderCompleted(@RequestBody UUID orderId) {
        return orderService.orderCompleted(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotal(@RequestBody UUID orderId) {
        return orderService.calculateTotal(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateDelivery(@RequestBody UUID orderId) {
        return orderService.calculateDelivery(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto orderAssembly(@RequestBody UUID orderId) {
        return orderService.orderAssembly(orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto orderAssemblyFailed(@RequestBody UUID orderId) {
        return orderService.orderAssemblyFailed(orderId);
    }
}
