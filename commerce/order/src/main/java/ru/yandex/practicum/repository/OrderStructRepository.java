package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.domain.Order;
import ru.yandex.practicum.domain.OrderStruct;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface OrderStructRepository extends JpaRepository<OrderStruct, UUID> {
    List<OrderStruct> findAllByOrder(Order order);

    List<OrderStruct> findAllByOrderIn(Collection<Order> orders);
}