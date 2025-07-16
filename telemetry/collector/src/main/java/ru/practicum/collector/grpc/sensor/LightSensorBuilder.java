package ru.practicum.collector.grpc.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.sensor.LightSensorEvent;
import ru.practicum.collector.model.sensor.SensorEvent;
import ru.practicum.collector.service.KafkaSensorEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Component
public class LightSensorBuilder extends AbstractSensorBuilder {

    public LightSensorBuilder(KafkaSensorEventProducer sensorEventProducer) {
        super(sensorEventProducer);
    }

    @Override
    public SensorEvent toSensorEvent(SensorEventProto sensorEvent) {
        LightSensorEvent lightSensorEvent = new LightSensorEvent(
                sensorEvent.getLightSensor().getLinkQuality(),
                sensorEvent.getLightSensor().getLuminosity()
        );
        lightSensorEvent.setId(sensorEvent.getId());
        lightSensorEvent.setHubId(sensorEvent.getHubId());
        lightSensorEvent.setTimestamp(Instant.ofEpochSecond(sensorEvent.getTimestamp()));
        return lightSensorEvent;
    }

    @Override
    public SensorEventProto.PayloadCase getPayloadCase() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }
}
