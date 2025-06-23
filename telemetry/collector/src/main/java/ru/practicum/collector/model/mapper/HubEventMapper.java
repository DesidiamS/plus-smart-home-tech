package ru.practicum.collector.model.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.DeviceAction;
import ru.practicum.collector.model.ScenarioCondition;
import ru.practicum.collector.model.ScenarioOperationType;
import ru.practicum.collector.model.hub.DeviceAddedEvent;
import ru.practicum.collector.model.hub.DeviceRemovedEvent;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.hub.ScenarioAddedEvent;
import ru.practicum.collector.model.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HubEventMapper {

    public DeviceAddedEventAvro toDeviceAddedEventAvro(HubEvent data) {
        DeviceAddedEvent deviceAddedEvent = (DeviceAddedEvent) data;
        return DeviceAddedEventAvro.newBuilder()
                .setId(deviceAddedEvent.getId())
                .setType(DeviceTypeAvro.valueOf(deviceAddedEvent.getDeviceType().toString()))
                .build();
    }

    public DeviceRemovedEventAvro toDeviceRemovedEventAvro(HubEvent data) {
        DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) data;
        return DeviceRemovedEventAvro.newBuilder()
                .setId(deviceRemovedEvent.getId())
                .build();
    }

    public ScenarioAddedEventAvro toScenarioAddedEventAvro(HubEvent data) {
        ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) data;
        return ScenarioAddedEventAvro.newBuilder()
                .setConditions(convertConditionsToAvro(scenarioAddedEvent.getConditions()))
                .setActions(convertActionsToAvro(scenarioAddedEvent.getActions()))
                .setName(scenarioAddedEvent.getName())
                .build();
    }

    public ScenarioRemovedEventAvro toScenarioRemovedEventAvro(HubEvent data) {
        ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) data;
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(scenarioRemovedEvent.getName())
                .build();
    }

    private List<ScenarioConditionAvro> convertConditionsToAvro(List<ScenarioCondition> conditions) {
        return conditions.stream()
                .map(this::convertConditionToAvro)
                .collect(Collectors.toList());
    }

    private ScenarioConditionAvro convertConditionToAvro(ScenarioCondition condition) {
        return ScenarioConditionAvro.newBuilder()
                .setOperation(operationTypeToAvro(condition.getOperation()))
                .setSensorId(condition.getSensorId())
                .setValue(condition.getValue())
                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                .build();
    }

    private ConditionOperationAvro operationTypeToAvro(ScenarioOperationType operationType) {
        return ConditionOperationAvro.valueOf(operationType.name());
    }

    private List<DeviceActionAvro> convertActionsToAvro(List<DeviceAction> actions) {
        List<DeviceActionAvro> result = new ArrayList<>();
        for (DeviceAction action : actions) {
            DeviceActionAvro avro = new DeviceActionAvro();

            avro.setSensorId(action.getSensorId());
            avro.setValue(action.getValue());
            avro.setType(ActionTypeAvro.valueOf(action.getType().toString()));

            result.add(avro);
        }

        return result;
    }

}
