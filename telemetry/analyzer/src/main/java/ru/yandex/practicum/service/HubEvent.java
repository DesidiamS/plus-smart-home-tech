package ru.yandex.practicum.service;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Component
public class HubEvent {

    private final Map<String, HubEventService> services;

    public HubEvent(List<HubEventService> services) {
        this.services = services.stream()
                .collect(Collectors.toMap(HubEventService::getEventType, Function.identity()));
    }
}
