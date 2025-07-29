package ru.practicum.collector.grpc.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.sensor.SensorEvent;
import ru.practicum.collector.model.sensor.SwitchSensorEvent;
import ru.practicum.collector.service.KafkaSensorEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Component
public class SwitchSensorBuilder extends AbstractSensorBuilder {

    public SwitchSensorBuilder(KafkaSensorEventProducer sensorEventProducer) {
        super(sensorEventProducer);
    }

    @Override
    public SensorEvent toSensorEvent(SensorEventProto sensorEvent) {
        SwitchSensorEvent switchSensorEvent = new SwitchSensorEvent(
                sensorEvent.getSwitchSensorEvent().getState()
        );
        switchSensorEvent.setId(sensorEvent.getId());
        switchSensorEvent.setHubId(sensorEvent.getHubId());
        switchSensorEvent.setTimestamp(Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(), sensorEvent.getTimestamp().getNanos()));
        return switchSensorEvent;
    }

    @Override
    public SensorEventProto.PayloadCase getPayloadCase() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }
}
