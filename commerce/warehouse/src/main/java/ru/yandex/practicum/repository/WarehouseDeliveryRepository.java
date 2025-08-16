package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.domain.WarehouseDelivery;

import java.util.Optional;
import java.util.UUID;

public interface WarehouseDeliveryRepository extends JpaRepository<WarehouseDelivery, UUID> {
    Optional<WarehouseDelivery> findByOrderId(UUID orderId);
}