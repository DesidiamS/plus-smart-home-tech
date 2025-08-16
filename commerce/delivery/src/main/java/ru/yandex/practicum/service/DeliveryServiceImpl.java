package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.domain.Address;
import ru.yandex.practicum.domain.Delivery;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.feign.OrderFeign;
import ru.yandex.practicum.feign.WarehouseFeign;
import ru.yandex.practicum.mapper.AddressMapper;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.DeliveryState;
import ru.yandex.practicum.repository.AddressRepository;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryMapper deliveryMapper;
    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;
    private final DeliveryRepository deliveryRepository;
    private final WarehouseFeign warehouseFeign;
    private final OrderFeign orderFeign;

    private static final BigDecimal BASE_COST = BigDecimal.valueOf(5);
    private static final String ADDRESS1 = "ADDRESS_1";
    private static final String ADDRESS2 = "ADDRESS_2";

    @Override
    public DeliveryDto create(DeliveryDto deliveryDto) {

        Address addressTo = addressMapper.AddressDtotoAddress(deliveryDto.getToAddress());
        Address addressFrom = addressMapper.AddressDtotoAddress(deliveryDto.getFromAddress());

        addressRepository.save(addressFrom);
        addressRepository.save(addressTo);

        Delivery delivery = deliveryMapper.deliveryDtoToDelivery(deliveryDto);

        return deliveryMapper.deliveryToDeliveryDto(deliveryRepository.save(delivery));
    }

    @Override
    public void deliverySuccess(UUID deliveryId) {
        Delivery delivery = changeDeliveryState(deliveryId, DeliveryState.DELIVERED);

        orderFeign.deliveryOrder(delivery.getOrderId());
    }

    @Override
    public void deliveryPicked(UUID deliveryId) {
        changeDeliveryState(deliveryId, DeliveryState.IN_PROGRESS);
    }

    @Override
    public void deliveryFailure(UUID deliveryId) {
        Delivery delivery = changeDeliveryState(deliveryId, DeliveryState.FAILED);

        orderFeign.deliveryOrderFailed(delivery.getOrderId());
    }

    @Override
    public BigDecimal calculateDeliveryPrice(OrderDto orderDto) {
        Delivery delivery = deliveryRepository.findByOrderId(orderDto.getOrderId()).orElseThrow(() ->
                new NoDeliveryFoundException("Информация о доставке не найдена!"));

        AddressDto warehouseAddress = warehouseFeign.checkAddress();

        BigDecimal deliveryPrice = switch (warehouseAddress.getCity()) {
            case ADDRESS1 -> BASE_COST;
            case ADDRESS2 -> BASE_COST.multiply(BigDecimal.valueOf(2));
            default -> throw new IllegalStateException(String.format("Unexpected value: %s", warehouseAddress.getCity()));
        };

        if (orderDto.getFragile()) {
            deliveryPrice = deliveryPrice.multiply(BigDecimal.valueOf(0.2));
        }

        deliveryPrice = deliveryPrice.add(BigDecimal.valueOf(orderDto.getDeliveryWeight() * 0.3));
        deliveryPrice =  deliveryPrice.add(BigDecimal.valueOf(orderDto.getDeliveryVolume() * 0.2));

        if (!warehouseAddress.getStreet().equals(delivery.getToAddress().getStreet())) {
            deliveryPrice = deliveryPrice.multiply(BigDecimal.valueOf(0.2));
        }

        return deliveryPrice;
    }

    private Delivery changeDeliveryState(UUID deliveryId, DeliveryState newDeliveryState) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() ->
                new NoDeliveryFoundException("Информация о доставке не найдена!"));

        delivery.setDeliveryState(newDeliveryState);

        return deliveryRepository.save(delivery);
    }
}
