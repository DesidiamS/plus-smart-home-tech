package ru.yandex.practicum.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "delivery-feign", path = "/api/v1/delivery")
@Validated
public interface DeliveryFeign {

    @PutMapping
    DeliveryDto createDelivery(@RequestBody @Valid DeliveryDto deliveryDto);

    @PostMapping("/successful")
    void successDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/picked")
    void pickDelivery(@RequestBody UUID deliveryId);

    @PostMapping("failed")
    void failDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/cost")
    BigDecimal calculateDelivery(@RequestBody @Valid OrderDto orderDto);
}
