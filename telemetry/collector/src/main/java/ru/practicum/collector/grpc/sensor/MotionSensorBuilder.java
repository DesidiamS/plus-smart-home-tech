package ru.practicum.collector.grpc.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.sensor.MotionSensorEvent;
import ru.practicum.collector.model.sensor.SensorEvent;
import ru.practicum.collector.service.KafkaSensorEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Component
public class MotionSensorBuilder extends AbstractSensorBuilder {

    public MotionSensorBuilder(KafkaSensorEventProducer sensorEventProducer) {
        super(sensorEventProducer);
    }

    @Override
    public SensorEvent toSensorEvent(SensorEventProto sensorEvent) {
        MotionSensorEvent motionSensorEvent = new MotionSensorEvent(
                sensorEvent.getMotionSensorEvent().getLinkQuality(),
                sensorEvent.getMotionSensorEvent().getMotion(),
                sensorEvent.getMotionSensorEvent().getVoltage()
        );
        motionSensorEvent.setId(sensorEvent.getId());
        motionSensorEvent.setHubId(sensorEvent.getHubId());
        motionSensorEvent.setTimestamp(Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(), sensorEvent.getTimestamp().getNanos()));
        return motionSensorEvent;
    }

    @Override
    public SensorEventProto.PayloadCase getPayloadCase() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }
}
