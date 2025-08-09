package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.domain.ShoppingCart;
import ru.yandex.practicum.domain.ShoppingCartProduct;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper
public class ShoppingCartMapper {

    public static ShoppingCartDto toShoppingCartDto(ShoppingCart shoppingCart, List<ShoppingCartProduct> shoppingCartProducts) {
        Map<UUID, Integer> productIds = shoppingCartProducts.stream()
                .collect(Collectors.toMap(ShoppingCartProduct::getProductId, ShoppingCartProduct::getQuantity));
        return new ShoppingCartDto(shoppingCart.getId(), productIds);
    }
}
