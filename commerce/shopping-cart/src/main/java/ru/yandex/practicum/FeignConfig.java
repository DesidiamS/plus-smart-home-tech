package ru.yandex.practicum;

import feign.Feign;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Feign.Builder warehouseFeignBuilder() {
        return Feign.builder().errorDecoder(new WarehouseErrorDecoder());
    }
}
