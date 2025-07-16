package ru.practicum.collector.grpc.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubBuilder {

    HubEventProto.PayloadCase getPayloadCase();

    void build(HubEventProto eventProto);
}
