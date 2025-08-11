package ru.yandex.practicum;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.feign.ShoppingStoreFeign;

@Configuration
@EnableFeignClients(clients = {ShoppingStoreFeign.class})
public class FeignConfig {
}
