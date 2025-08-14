package ru.practicum.collector.model.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.sensor.ClimateSensorEvent;
import ru.practicum.collector.model.sensor.LightSensorEvent;
import ru.practicum.collector.model.sensor.MotionSensorEvent;
import ru.practicum.collector.model.sensor.SensorEvent;
import ru.practicum.collector.model.sensor.SwitchSensorEvent;
import ru.practicum.collector.model.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
public class SensorEventMapper {

    public SensorEventAvro toSensorEventAvro(SensorEvent data) {
        Object payload = switch (data) {
            case ClimateSensorEvent climateSensorEvent -> ClimateSensorAvro.newBuilder()
                    .setCo2Level(climateSensorEvent.getCo2Level())
                    .setHumidity(climateSensorEvent.getHumidity())
                    .setTemperatureC(climateSensorEvent.getTemperatureC())
                    .build();
            case LightSensorEvent lightSensorEvent -> LightSensorAvro.newBuilder()
                    .setLinkQuality(lightSensorEvent.getLinkQuality())
                    .setLuminosity(lightSensorEvent.getLuminosity())
                    .build();
            case MotionSensorEvent motionSensorEvent -> MotionSensorAvro.newBuilder()
                    .setLinkQuality(motionSensorEvent.getLinkQuality())
                    .setMotion(motionSensorEvent.getMotion())
                    .setVoltage(motionSensorEvent.getVoltage())
                    .build();
            case SwitchSensorEvent switchSensorEvent -> SwitchSensorAvro.newBuilder()
                    .setState(switchSensorEvent.getState())
                    .build();
            case TemperatureSensorEvent temperatureSensorEvent -> TemperatureSensorAvro.newBuilder()
                    .setTemperatureC(temperatureSensorEvent.getTemperatureC())
                    .setTemperatureF(temperatureSensorEvent.getTemperatureF())
                    .setId(temperatureSensorEvent.getId())
                    .setHubId(temperatureSensorEvent.getHubId())
                    .setTimestamp(temperatureSensorEvent.getTimestamp())
                    .build();
            default -> throw new IllegalArgumentException("Unknown sensor event type");
        };

        return SensorEventAvro.newBuilder()
                .setHubId(data.getHubId())
                .setId(data.getId())
                .setTimestamp(data.getTimestamp())
                .setPayload(payload)
                .build();
    }
}
