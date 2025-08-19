package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.domain.Order;
import ru.yandex.practicum.domain.OrderStruct;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.feign.DeliveryFeign;
import ru.yandex.practicum.feign.PaymentFeign;
import ru.yandex.practicum.feign.ShoppingCartFeign;
import ru.yandex.practicum.feign.WarehouseFeign;
import ru.yandex.practicum.model.DeliveryState;
import ru.yandex.practicum.model.OrderStatus;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.repository.OrderStructRepository;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.yandex.practicum.mapper.OrderMapper.toOrderDto;
import static ru.yandex.practicum.mapper.OrderMapper.toOrderDtoList;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ShoppingCartFeign shoppingCartFeign;
    private final OrderStructRepository orderStructRepository;
    private final DeliveryFeign deliveryFeign;
    private final PaymentFeign paymentFeign;
    private final WarehouseFeign warehouseFeign;

    @Override
    public List<OrderDto> getUserOrders(String username) {
        ShoppingCartDto shoppingCartDto = shoppingCartFeign.getShoppingCart(username);

        List<Order> orders = orderRepository.findAllByShoppingCartId(shoppingCartDto.getShoppingCartId());
        List<OrderStruct> products = orderStructRepository.findAllByOrderIn(orders);

        return toOrderDtoList(orders, products);
    }

    @Override
    public OrderDto createOrder(CreateNewOrderRequest request) {
        Order order = new Order(
                OrderStatus.NEW,
                request.getShoppingCart().getShoppingCartId()
        );

        List<OrderStruct> orderStructs = new ArrayList<>();

        order = orderRepository.save(order);

        BookedProductsDto bookedProductsDto = warehouseFeign.assemblyProducts(new AssemblyProductsForOrderRequest(
                request.getShoppingCart().getProducts(),
                order.getId()
        ));

        order.setFragile(bookedProductsDto.getFragile());
        order.setDeliveryVolume(bookedProductsDto.getDeliveryVolume());
        order.setDeliveryWeight(bookedProductsDto.getDeliveryWeight());

        for (Map.Entry<UUID, Integer> product : request.getShoppingCart().getProducts().entrySet()) {
            orderStructs.add(new OrderStruct(
                    null,
                    order,
                    product.getKey(),
                    product.getValue()
            ));
        }

        order.setProductPrice(paymentFeign.calculateProductTotal(toOrderDto(order, orderStructs)));

        UUID deliveryId = deliveryFeign.createDelivery(new DeliveryDto(
                null,
                warehouseFeign.checkAddress(),
                request.getAddress(),
                order.getId(),
                DeliveryState.CREATED)).getDeliveryId();

        order.setDeliveryId(deliveryId);

        OrderDto newOrder = toOrderDto(order, orderStructRepository.saveAll(orderStructs));

        paymentFeign.createPayment(newOrder);

        return newOrder;
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        warehouseFeign.returnProducts(request.getProducts());
        return changeOrderStatus(request.getOrderId(), OrderStatus.PRODUCT_RETURNED);
    }

    @Override
    public OrderDto payOrder(UUID orderId) {
        return changeOrderStatus(orderId, OrderStatus.PAID);
    }

    @Override
    public OrderDto payOrderFailed(UUID orderId) {
        return changeOrderStatus(orderId, OrderStatus.PAYMENT_FAILED);
    }

    @Override
    public OrderDto deliverOrder(UUID orderId) {
        return changeOrderStatus(orderId, OrderStatus.DELIVERED);
    }

    @Override
    public OrderDto deliverOrderFailed(UUID orderId) {
        return changeOrderStatus(orderId, OrderStatus.DELIVERY_FAILED);
    }

    @Override
    public OrderDto orderCompleted(UUID orderId) {
        return changeOrderStatus(orderId, OrderStatus.COMPLETED);
    }

    @Override
    public OrderDto calculateTotal(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new NoOrderFoundException("Заказ не найден!"));
        OrderDto orderDto = toOrderDto(order, orderStructRepository.findAllByOrder(order));

        orderDto.setTotalPrice(paymentFeign.calculateTotalCost(orderDto));

        return orderDto;
    }

    @Override
    public OrderDto calculateDelivery(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new NoOrderFoundException("Заказ не найден!"));
        OrderDto orderDto = toOrderDto(order, orderStructRepository.findAllByOrder(order));
        orderDto.setDeliveryPrice(deliveryFeign.calculateDelivery(orderDto));
        return orderDto;
    }

    @Override
    public OrderDto orderAssembly(UUID orderId) {
        return changeOrderStatus(orderId, OrderStatus.ASSEMBLED);
    }

    @Override
    public OrderDto orderAssemblyFailed(UUID orderId) {
        return changeOrderStatus(orderId, OrderStatus.ASSEMBLY_FAILED);
    }

    private OrderDto changeOrderStatus(UUID orderId, OrderStatus orderStatus) {

        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new NoOrderFoundException("Заказ не найден!"));

        order.setState(orderStatus);

        return toOrderDto(orderRepository.save(order), orderStructRepository.findAllByOrder(order));
    }
}
