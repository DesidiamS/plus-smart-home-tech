package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.domain.Sensor;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

@Service
@RequiredArgsConstructor
public class DeviceAddedService implements HubEventService {

    private final SensorRepository sensorRepository;

    @Override
    @Transactional
    public void save(HubEventAvro event) {
        DeviceAddedEventAvro deviceAddedEventAvro = (DeviceAddedEventAvro) event.getPayload();

        Sensor sensor = new Sensor(deviceAddedEventAvro.getId(), event.getHubId());

        sensorRepository.save(sensor);
    }

    @Override
    public String getEventType() {
        return DeviceAddedEventAvro.class.getSimpleName();
    }
}
