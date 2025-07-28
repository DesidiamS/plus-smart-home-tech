package ru.practicum.collector.grpc.hub;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.hub.ScenarioRemovedEvent;
import ru.practicum.collector.model.mapper.HubEventMapper;
import ru.practicum.collector.service.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.time.Instant;

@Component
public class ScenarioRemovedBuilder extends AbstractHubBuilder {

    public ScenarioRemovedBuilder(KafkaEventProducer kafkaEventProducer, HubEventMapper hubEventMapper) {
        super(kafkaEventProducer, hubEventMapper);
    }

    @Override
    public HubEvent toHubEvent(HubEventProto hubEventProto) {
        ScenarioRemovedEvent scenarioRemovedEvent = new ScenarioRemovedEvent(
                hubEventProto.getScenarioRemovedEvent().getName());
        scenarioRemovedEvent.setHubId(hubEventProto.getHubId());
        scenarioRemovedEvent.setTimestamp(Instant.ofEpochSecond(hubEventProto.getTimestamp().getSeconds(), hubEventProto.getTimestamp().getNanos()));
        return scenarioRemovedEvent;
    }

    @Override
    public HubEventProto.PayloadCase getPayloadCase() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED_EVENT;
    }
}
