package ru.practicum.collector.grpc.sensor;

import lombok.RequiredArgsConstructor;
import ru.practicum.collector.model.mapper.SensorEventMapper;
import ru.practicum.collector.model.sensor.SensorEvent;
import ru.practicum.collector.service.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@RequiredArgsConstructor
public abstract class AbstractSensorBuilder implements SensorBuilder {

    private final KafkaEventProducer kafkaEventProducer;
    private final SensorEventMapper mapper;

    public abstract SensorEvent toSensorEvent(SensorEventProto sensorEvent);

    @Override
    public abstract SensorEventProto.PayloadCase getPayloadCase();

    @Override
    public void build(SensorEventProto sensorEventProto) {
        SensorEvent sensorEvent = toSensorEvent(sensorEventProto);
        kafkaEventProducer.send(mapper.toSensorEventAvro(sensorEvent), "telemetry.sensors.v1");
    }
}
