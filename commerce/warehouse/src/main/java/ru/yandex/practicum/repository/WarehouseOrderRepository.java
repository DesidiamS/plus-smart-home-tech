package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.domain.WarehouseOrder;

import java.util.UUID;

public interface WarehouseOrderRepository extends JpaRepository<WarehouseOrder, UUID> {
}