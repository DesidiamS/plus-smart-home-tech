package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.domain.ShoppingCart;
import ru.yandex.practicum.domain.ShoppingCartProduct;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ShoppingCartProductRepository extends JpaRepository<ShoppingCartProduct, UUID> {
    List<ShoppingCartProduct> getShoppingCartProductsByCart(ShoppingCart cart);

    void deleteByProductIdInAndCart(Collection<UUID> productIds, ShoppingCart cart);
}