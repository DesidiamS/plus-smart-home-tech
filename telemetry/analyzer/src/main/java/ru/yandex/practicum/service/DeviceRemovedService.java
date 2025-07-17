package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.domain.Sensor;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeviceRemovedService implements HubEventService {

    private final SensorRepository sensorRepository;

    @Override
    @Transactional
    public void save(HubEventAvro event) {
        DeviceRemovedEventAvro deviceRemovedEventAvro = (DeviceRemovedEventAvro) event.getPayload();
        Optional<Sensor> sensor = sensorRepository.findByIdAndHubId(deviceRemovedEventAvro.getId(), event.getHubId());
        sensor.ifPresent(sensorRepository::delete);
    }

    @Override
    public String getEventType() {
        return DeviceRemovedEventAvro.class.getSimpleName();
    }
}
