package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

    PaymentDto createPayment(OrderDto orderDto);

    BigDecimal calculateTotalPayment(OrderDto orderDto);

    void refundPayment(UUID paymentId);

    BigDecimal calculateProductTotal(OrderDto orderDto);

    void failPayment(UUID paymentId);
}
