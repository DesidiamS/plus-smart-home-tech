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
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
public class SensorEventMapper {

    public ClimateSensorAvro toClimateSensorAvro(SensorEvent data) {
        ClimateSensorEvent climateSensorEvent = (ClimateSensorEvent) data;
        return ClimateSensorAvro.newBuilder()
                .setCo2Level(climateSensorEvent.getCo2Level())
                .setHumidity(climateSensorEvent.getHumidity())
                .setTemperatureC(climateSensorEvent.getTemperatureC())
                .build();
    }

    public LightSensorAvro toLightSensorAvro(SensorEvent data) {
        LightSensorEvent lightSensorEvent = (LightSensorEvent) data;
        return LightSensorAvro.newBuilder()
                .setLinkQuality(lightSensorEvent.getLinkQuality())
                .setLuminosity(lightSensorEvent.getLuminosity())
                .build();
    }

    public MotionSensorAvro toMotionSensorAvro(SensorEvent data) {
        MotionSensorEvent motionSensorEvent = (MotionSensorEvent) data;
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(motionSensorEvent.getLinkQuantity())
                .setMotion(motionSensorEvent.getMotion())
                .setVoltage(motionSensorEvent.getVoltage())
                .build();
    }

    public SwitchSensorAvro toSwitchSensorAvro(SensorEvent data) {
        SwitchSensorEvent switchSensorEvent = (SwitchSensorEvent) data;
        return SwitchSensorAvro.newBuilder()
                .setState(switchSensorEvent.getState())
                .build();
    }

    public TemperatureSensorAvro toTemperatureSensorAvro(SensorEvent data) {
        TemperatureSensorEvent temperatureSensorEvent = (TemperatureSensorEvent) data;
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(temperatureSensorEvent.getTemperatureC())
                .setTemperatureF(temperatureSensorEvent.getTemperatureF())
                .setId(temperatureSensorEvent.getId())
                .setHubId(temperatureSensorEvent.getHubId())
                .setTimestamp(temperatureSensorEvent.getTimestamp())
                .build();
    }
}
