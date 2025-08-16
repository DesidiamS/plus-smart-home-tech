package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto createDelivery(@RequestBody DeliveryDto deliveryDto) {
        return deliveryService.create(deliveryDto);
    }

    @PostMapping("/successful")
    public void successDelivery(@RequestBody UUID deliveryId) {
        deliveryService.deliverySuccess(deliveryId);
    }

    @PostMapping("/picked")
    public void pickDelivery(@RequestBody UUID deliveryId) {
        deliveryService.deliveryPicked(deliveryId);
    }

    @PostMapping("/failed")
    public void failDelivery(@RequestBody UUID deliveryId) {
        deliveryService.deliveryFailure(deliveryId);
    }

    @PostMapping("/cost")
    public BigDecimal calculateDelivery(@RequestBody OrderDto orderDto) {
        return deliveryService.calculateDeliveryPrice(orderDto);
    }
}
