package ru.yandex.practicum.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.model.ProductCategory;
import ru.yandex.practicum.model.ProductState;
import ru.yandex.practicum.model.QuantityState;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "products")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    UUID productId;
    @NotNull
    String productName;
    @NotNull
    String description;
    @Column(name = "image_src")
    String imageSrc;
    Double price;
    @Column(name = "quantity_state")
    @Enumerated(EnumType.STRING)
    QuantityState quantityState;
    @Column(name = "product_state")
    @Enumerated(EnumType.STRING)
    ProductState productState;
    @Column(name = "product_category")
    @Enumerated(EnumType.STRING)
    ProductCategory productCategory;
}
