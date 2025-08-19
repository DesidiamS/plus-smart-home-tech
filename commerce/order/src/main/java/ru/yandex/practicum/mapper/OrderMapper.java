package ru.yandex.practicum.mapper;

import ru.yandex.practicum.domain.Order;
import ru.yandex.practicum.domain.OrderStruct;
import ru.yandex.practicum.dto.OrderDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderDto toOrderDto(Order order, List<OrderStruct> orderStructs) {
        Map<UUID, Integer> products = orderStructs.stream()
                .filter(orderStruct -> orderStruct.getOrder().equals(order))
                .collect(Collectors.toMap(OrderStruct::getProductId, OrderStruct::getQuantity));
        return new OrderDto(
                order.getId(),
                order.getShoppingCartId(),
                products,
                order.getPaymentId(),
                order.getDeliveryId(),
                order.getState(),
                order.getDeliveryWeight(),
                order.getDeliveryVolume(),
                order.getFragile(),
                order.getTotalPrice(),
                order.getDeliveryPrice(),
                order.getProductPrice()
        );
    }

    public static List<OrderDto> toOrderDtoList(List<Order> orders, List<OrderStruct> orderStructs) {
        List<OrderDto> orderDtoList = new ArrayList<>();
        for (Order order : orders) {
            orderDtoList.add(toOrderDto(order, orderStructs));
        }

        return orderDtoList;
    }

}
