package ru.practicum.collector.grpc.hub;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.hub.DeviceRemovedEvent;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.mapper.HubEventMapper;
import ru.practicum.collector.service.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.time.Instant;

@Component
public class DeviceRemovedBuilder extends AbstractHubBuilder {

    public DeviceRemovedBuilder(KafkaEventProducer kafkaEventProducer, HubEventMapper hubEventMapper) {
        super(kafkaEventProducer, hubEventMapper);
    }

    @Override
    public HubEvent toHubEvent(HubEventProto hubEventProto) {
        DeviceRemovedEvent deviceRemovedEvent = new DeviceRemovedEvent(
                hubEventProto.getDeviceRemovedEvent().getId()
        );
        deviceRemovedEvent.setHubId(hubEventProto.getHubId());
        deviceRemovedEvent.setTimestamp(Instant.ofEpochSecond(hubEventProto.getTimestamp().getSeconds(), hubEventProto.getTimestamp().getNanos()));
        return deviceRemovedEvent;
    }

    @Override
    public HubEventProto.PayloadCase getPayloadCase() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED_EVENT;
    }
}
