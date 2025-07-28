package ru.practicum.collector.grpc.hub;

import lombok.RequiredArgsConstructor;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.mapper.HubEventMapper;
import ru.practicum.collector.service.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@RequiredArgsConstructor
public abstract class AbstractHubBuilder implements HubBuilder {

    private final KafkaEventProducer kafkaEventProducer;
    private final HubEventMapper mapper;

    public abstract HubEvent toHubEvent(HubEventProto hubEventProto);

    @Override
    public abstract HubEventProto.PayloadCase getPayloadCase();

    @Override
    public void build(HubEventProto hubEventProto) {
        HubEvent hubEvent = toHubEvent(hubEventProto);
        kafkaEventProducer.send(mapper.toHubEventAvro(hubEvent), "telemetry.hubs.v1");
    }
}
