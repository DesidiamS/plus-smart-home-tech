package ru.practicum.collector.grpc.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.sensor.SensorEvent;
import ru.practicum.collector.model.sensor.SwitchSensorEvent;
import ru.practicum.collector.service.KafkaSensorEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Component
public class SwitchSensorBuilder extends SensorBuilder {

    public SwitchSensorBuilder(KafkaSensorEventProducer sensorEventProducer) {
        super(sensorEventProducer);
    }

    @Override
    public SensorEvent toSensorEvent(SensorEventProto sensorEvent) {
        SwitchSensorEvent switchSensorEvent = new SwitchSensorEvent(
                sensorEvent.getSwitchSensor().isInitialized()
        );
        switchSensorEvent.setId(sensorEvent.getId());
        switchSensorEvent.setHubId(sensorEvent.getHubId());
        switchSensorEvent.setTimestamp(Instant.ofEpochMilli(sensorEvent.getTimestamp()));
        return switchSensorEvent;
    }

    @Override
    public SensorEventProto.PayloadCase getPayloadCase() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }
}
