package ru.practicum.collector.grpc.sensor;

import lombok.RequiredArgsConstructor;
import ru.practicum.collector.model.sensor.SensorEvent;
import ru.practicum.collector.service.KafkaSensorEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@RequiredArgsConstructor
public abstract class SensorBuilder {

    private final KafkaSensorEventProducer sensorEventProducer;

    public abstract SensorEvent toSensorEvent(SensorEventProto sensorEvent);

    public abstract SensorEventProto.PayloadCase getPayloadCase();

    public void build(SensorEventProto sensorEventProto) {
        SensorEvent sensorEvent = toSensorEvent(sensorEventProto);
        sensorEventProducer.send(sensorEvent);
    }
}
