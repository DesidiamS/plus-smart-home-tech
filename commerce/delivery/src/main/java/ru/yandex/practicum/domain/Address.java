package ru.yandex.practicum.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID addressId;

    @Column(name = "country", length = Integer.MAX_VALUE)
    private String country;

    @Column(name = "city", length = Integer.MAX_VALUE)
    private String city;

    @Column(name = "street", length = Integer.MAX_VALUE)
    private String street;

    @Column(name = "house", length = Integer.MAX_VALUE)
    private String house;

    @Column(name = "flat", length = Integer.MAX_VALUE)
    private String flat;

}