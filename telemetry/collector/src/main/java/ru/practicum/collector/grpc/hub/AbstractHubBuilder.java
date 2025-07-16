package ru.practicum.collector.grpc.hub;

import lombok.RequiredArgsConstructor;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.service.KafkaHubEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@RequiredArgsConstructor
public abstract class AbstractHubBuilder implements HubBuilder {

    private final KafkaHubEventProducer kafkaHubEventProducer;

    public abstract HubEvent toHubEvent(HubEventProto hubEventProto);

    public abstract HubEventProto.PayloadCase getPayloadCase();

    public void build(HubEventProto hubEventProto) {
        HubEvent hubEvent = toHubEvent(hubEventProto);
        kafkaHubEventProducer.send(hubEvent);
    }
}
