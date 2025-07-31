package ru.practicum.collector.grpc.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorBuilder {

    SensorEventProto.PayloadCase getPayloadCase();

    void build(SensorEventProto eventProto);
}
