package ru.yandex.practicum.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.model.QuantityState;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SetProductQuantityStateRequest {

    @NotBlank
    String productId;
    @NotBlank
    QuantityState quantityState;
}
