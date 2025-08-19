package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.domain.Payment;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.feign.OrderFeign;
import ru.yandex.practicum.feign.ShoppingStoreFeign;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.PaymentStatus;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreFeign shoppingStoreFeign;
    private final OrderFeign orderFeign;

    @Override
    @Transactional
    public PaymentDto createPayment(OrderDto orderDto) {
        Payment payment = new Payment(
                null,
                orderDto.getOrderId(),
                orderDto.getTotalPrice(),
                orderDto.getProductPrice(),
                orderDto.getDeliveryPrice(),
                orderDto.getTotalPrice().multiply(BigDecimal.valueOf(0.1)),
                PaymentStatus.IN_PROGRESS
        );

        return paymentMapper.toPaymentDto(paymentRepository.save(payment));
    }

    @Override
    public BigDecimal calculateTotalPayment(OrderDto orderDto) {
        return orderDto.getProductPrice().multiply(BigDecimal.valueOf(0.1))
                .add(orderDto.getDeliveryPrice()).add(orderDto.getProductPrice());
    }

    @Override
    @Transactional
    public void refundPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() ->
                new NotFoundException("Payment with id: " + paymentId));

        payment.setStatus(PaymentStatus.SUCCESS);

        paymentRepository.save(payment);
        orderFeign.payOrder(payment.getOrderId());
    }

    @Override
    public BigDecimal calculateProductTotal(OrderDto orderDto) {
        BigDecimal productPrice = BigDecimal.ZERO;
        if (orderDto.getProducts() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("В заказе нет товаров");
        }

        for (Map.Entry<UUID, Integer> entry : orderDto.getProducts().entrySet()) {
            ProductDto product = shoppingStoreFeign.getProduct(String.valueOf(entry.getKey()));
            productPrice = productPrice.add(product.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
        }

        return productPrice;
    }

    @Override
    @Transactional
    public void failPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() ->
                new  NotFoundException("Payment with id: " + paymentId));
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
        orderFeign.payOrderFailed(payment.getOrderId());
    }
}
