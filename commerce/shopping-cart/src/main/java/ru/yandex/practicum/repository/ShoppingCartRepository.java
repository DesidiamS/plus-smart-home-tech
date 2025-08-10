package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.domain.ShoppingCart;

import java.util.UUID;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, UUID> {

    @Query("SELECT c FROM ShoppingCart c WHERE c.username = ?1 ORDER BY c.id LIMIT 1")
    ShoppingCart findLastShoppingCartByUsernameIgnoreCase(String username);
}