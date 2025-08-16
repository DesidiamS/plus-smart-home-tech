package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto create(DeliveryDto deliveryDto);

    void deliverySuccess(UUID deliveryId);

    void deliveryPicked(UUID deliveryId);

    void deliveryFailure(UUID deliveryId);

    BigDecimal calculateDeliveryPrice(OrderDto orderDto);


}
