package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentDto createPayment(@RequestBody OrderDto orderDto) {
       return paymentService.createPayment(orderDto);
    }

    @PostMapping("/totalCost")
    public BigDecimal calculateTotalCost(@RequestBody OrderDto orderDto) {
        return paymentService.calculateTotalPayment(orderDto);
    }

    @PostMapping("/refund")
    public void refundPayment(@RequestBody UUID paymentId) {
        paymentService.refundPayment(paymentId);
    }

    @PostMapping("/productCost")
    public BigDecimal calculateProductTotal(@RequestBody OrderDto orderDto) {
        return paymentService.calculateProductTotal(orderDto);
    }

    @PostMapping("/failed")
    public void paymentFailed(@RequestBody UUID paymentId) {
        paymentService.failPayment(paymentId);
    }
}
