package ru.practicum.collector.grpc.hub;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.hub.DeviceAddedEvent;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.mapper.HubEventMapper;
import ru.practicum.collector.model.sensor.SensorEventType;
import ru.practicum.collector.service.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.time.Instant;

@Component
public class DeviceAddedBuilder extends AbstractHubBuilder {

    public DeviceAddedBuilder(KafkaEventProducer kafkaEventProducer, HubEventMapper hubEventMapper) {
        super(kafkaEventProducer, hubEventMapper);
    }

    @Override
    public HubEvent toHubEvent(HubEventProto hubEventProto) {
        SensorEventType sensorEventType = null;

        DeviceTypeProto deviceTypeProto = hubEventProto.getDeviceAddedEvent().getType();

        switch (deviceTypeProto) {
            case CLIMATE_SENSOR -> sensorEventType = SensorEventType.CLIMATE_SENSOR;
            case LIGHT_SENSOR -> sensorEventType = SensorEventType.LIGHT_SENSOR;
            case MOTION_SENSOR -> sensorEventType = SensorEventType.MOTION_SENSOR;
            case TEMPERATURE_SENSOR -> sensorEventType = SensorEventType.TEMPERATURE_SENSOR;
            case SWITCH_SENSOR -> sensorEventType = SensorEventType.SWITCH_SENSOR;
        }

        DeviceAddedEvent deviceAddedEvent = new DeviceAddedEvent(
                hubEventProto.getDeviceAddedEvent().getId(),
                sensorEventType
        );
        deviceAddedEvent.setHubId(hubEventProto.getHubId());
        deviceAddedEvent.setTimestamp(Instant.ofEpochSecond(hubEventProto.getTimestamp().getSeconds(), hubEventProto.getTimestamp().getNanos()));
        return deviceAddedEvent;
    }

    @Override
    public HubEventProto.PayloadCase getPayloadCase() {
        return HubEventProto.PayloadCase.DEVICE_ADDED_EVENT;
    }
}
