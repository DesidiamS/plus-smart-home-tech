package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.domain.ShoppingCart;
import ru.yandex.practicum.domain.ShoppingCartProduct;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.feign.WarehouseFeign;
import ru.yandex.practicum.repository.ShoppingCartProductRepository;
import ru.yandex.practicum.repository.ShoppingCartRepository;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.yandex.practicum.mapper.ShoppingCartMapper.toShoppingCartDto;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartProductRepository shoppingCartProductRepository;
    private final WarehouseFeign warehouseFeign;

    @Override
    public ShoppingCartDto getShoppingCartByUsername(String username) {
        ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartByUsernameContainsIgnoreCase(username);
        List<ShoppingCartProduct> products = shoppingCartProductRepository.getShoppingCartProductsByCart(shoppingCart);
        return toShoppingCartDto(shoppingCart, products);
    }

    @Override
    public ShoppingCartDto addToCart(String username, Map<UUID, Integer> items) {
        ShoppingCart cart = new ShoppingCart(null, username, true);
        List<ShoppingCartProduct> products = items.entrySet().stream()
                .map(item -> new ShoppingCartProduct(null, item.getKey(), item.getValue(), cart))
                .toList();

        shoppingCartRepository.save(cart);
        shoppingCartProductRepository.saveAll(products);

        return toShoppingCartDto(cart, products);
    }

    @Override
    public void deactivateCart(String username) {
        ShoppingCart cart = shoppingCartRepository.findShoppingCartByUsernameContainsIgnoreCase(username);
        cart.setActive(false);
        shoppingCartRepository.save(cart);
    }

    @Override
    public ShoppingCartDto removeFromCart(String username, List<UUID> items) {
        ShoppingCart cart = shoppingCartRepository.findShoppingCartByUsernameContainsIgnoreCase(username);

        shoppingCartProductRepository.deleteByProductIdInAndCart(items, cart);

        List<ShoppingCartProduct> products = shoppingCartProductRepository.getShoppingCartProductsByCart(cart);

        return toShoppingCartDto(cart, products);
    }

    @Override
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest items) {
        ShoppingCart cart = shoppingCartRepository.findShoppingCartByUsernameContainsIgnoreCase(username);

        List<ShoppingCartProduct> products = shoppingCartProductRepository.getShoppingCartProductsByCart(cart);

        products.stream()
                .filter(product -> product.getProductId().equals(items.getProductId()))
                .findFirst()
                .ifPresent(product -> product.setQuantity(items.getQuantity()));

        products = shoppingCartProductRepository.saveAll(products);

        return toShoppingCartDto(cart, products);
    }

    @Override
    public BookedProductsDto buyProductsInCart(String username) {
        ShoppingCart cart = shoppingCartRepository.findShoppingCartByUsernameContainsIgnoreCase(username);
        List<ShoppingCartProduct> products = shoppingCartProductRepository.getShoppingCartProductsByCart(cart);

        return warehouseFeign.buyProduct(toShoppingCartDto(cart, products));
    }
}
