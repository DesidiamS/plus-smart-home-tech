package ru.practicum.collector.grpc.hub;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.hub.DeviceRemovedEvent;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.service.KafkaHubEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.time.Instant;

@Component
public class DeviceRemovedBuilder extends AbstractHubBuilder {

    public DeviceRemovedBuilder(KafkaHubEventProducer kafkaHubEventProducer) {
        super(kafkaHubEventProducer);
    }

    @Override
    public HubEvent toHubEvent(HubEventProto hubEventProto) {
        DeviceRemovedEvent deviceRemovedEvent = new DeviceRemovedEvent(
                hubEventProto.getDeviceRemovedEvent().getId()
        );
        deviceRemovedEvent.setHubId(hubEventProto.getHubId());
        deviceRemovedEvent.setTimestamp(Instant.ofEpochMilli(hubEventProto.getTimestamp()));
        return deviceRemovedEvent;
    }

    @Override
    public HubEventProto.PayloadCase getPayloadCase() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED_EVENT;
    }
}
