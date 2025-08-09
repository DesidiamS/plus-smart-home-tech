package ru.yandex.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.domain.Product;
import ru.yandex.practicum.model.ProductCategory;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> getProductsByProductCategory(ProductCategory category, Pageable pageable);


}