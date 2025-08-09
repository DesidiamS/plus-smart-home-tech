package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PageableDto {

    @Min(0)
    Integer page = 0;
    @Min(1)
    Integer size = 1;
    List<String> sort = new ArrayList<>();
}
