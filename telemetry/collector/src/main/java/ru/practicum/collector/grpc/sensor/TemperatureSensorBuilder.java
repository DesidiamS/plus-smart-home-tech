package ru.practicum.collector.grpc.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.sensor.SensorEvent;
import ru.practicum.collector.model.sensor.TemperatureSensorEvent;
import ru.practicum.collector.service.KafkaSensorEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Component
public class TemperatureSensorBuilder extends AbstractSensorBuilder {

    public TemperatureSensorBuilder(KafkaSensorEventProducer sensorEventProducer) {
        super(sensorEventProducer);
    }

    @Override
    public SensorEvent toSensorEvent(SensorEventProto sensorEvent) {
        TemperatureSensorEvent temperatureSensorEvent = new TemperatureSensorEvent(
                sensorEvent.getTemperatureSensor().getTemperatureC(),
                sensorEvent.getTemperatureSensor().getTemperatureF()
        );
        temperatureSensorEvent.setId(sensorEvent.getId());
        temperatureSensorEvent.setHubId(sensorEvent.getHubId());
        temperatureSensorEvent.setTimestamp(Instant.ofEpochMilli(sensorEvent.getTimestamp()));
        return temperatureSensorEvent;
    }

    @Override
    public SensorEventProto.PayloadCase getPayloadCase() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }
}
