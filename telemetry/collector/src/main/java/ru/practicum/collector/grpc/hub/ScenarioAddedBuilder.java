package ru.practicum.collector.grpc.hub;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.DeviceAction;
import ru.practicum.collector.model.DeviceActionType;
import ru.practicum.collector.model.ScenarioCondition;
import ru.practicum.collector.model.ScenarioOperationType;
import ru.practicum.collector.model.ScenarioType;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.hub.ScenarioAddedEvent;
import ru.practicum.collector.service.KafkaHubEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScenarioAddedBuilder extends AbstractHubBuilder {

    public ScenarioAddedBuilder(KafkaHubEventProducer kafkaHubEventProducer) {
        super(kafkaHubEventProducer);
    }

    @Override
    public HubEvent toHubEvent(HubEventProto hubEventProto) {
        ScenarioAddedEvent scenarioAddedEvent = new ScenarioAddedEvent(
                hubEventProto.getScenarioAddedEvent().getName(),
                toScenarioConditions(hubEventProto.getScenarioAddedEvent().getConditionsList()),
                toDeviceAction(hubEventProto.getScenarioAddedEvent().getActionsList())
        );
        scenarioAddedEvent.setHubId(hubEventProto.getHubId());
        scenarioAddedEvent.setTimestamp(Instant.ofEpochSecond(hubEventProto.getTimestamp().getSeconds(), hubEventProto.getTimestamp().getNanos()));
        return scenarioAddedEvent;
    }

    @Override
    public HubEventProto.PayloadCase getPayloadCase() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED_EVENT;
    }

    protected List<ScenarioCondition> toScenarioConditions(List<ScenarioConditionProto> scenarioConditions) {
        List<ScenarioCondition> scenarioConditionList = new ArrayList<>();
        for (ScenarioConditionProto scenarioCondition : scenarioConditions) {
            ScenarioType scenarioType = toScenarioType(scenarioCondition.getType());
            ScenarioOperationType operationType = toOperationType(scenarioCondition.getOperation());
            scenarioConditionList.add(new ScenarioCondition(
                    scenarioCondition.getSensorId(),
                    scenarioType,
                    operationType,
                    switch (scenarioCondition.getValueCase()) {
                        case INT_VALUE -> scenarioCondition.getIntValue();
                        case BOOL_VALUE -> scenarioCondition.getBoolValue();
                        case VALUE_NOT_SET -> null;
                    })
            );
        }

        return scenarioConditionList;
    }

    protected ScenarioType toScenarioType(ConditionTypeProto conditionTypeProto) {
        return switch (conditionTypeProto) {
            case MOTION -> ScenarioType.MOTION;
            case LUMINOSITY -> ScenarioType.LUMINOSITY;
            case SWITCH -> ScenarioType.SWITCH;
            case TEMPERATURE -> ScenarioType.TEMPERATURE;
            case CO2LEVEL -> ScenarioType.CO2LEVEL;
            case HUMIDITY -> ScenarioType.HUMIDITY;
            default -> null;
        };
    }

    protected ScenarioOperationType toOperationType(ConditionOperationProto operationTypeProto) {
        switch (operationTypeProto) {
            case EQUALS -> {
                return ScenarioOperationType.EQUALS;
            }
            case GREATER_THAN -> {
                return ScenarioOperationType.GREATER_THAN;
            }
            case LOWER_THAN -> {
                return ScenarioOperationType.LOWER_THAN;
            }
            default -> {
                return null;
            }
        }
    }

    protected List<DeviceAction> toDeviceAction(List<DeviceActionProto> deviceActionsProto) {
        List<DeviceAction> deviceActionList = new ArrayList<>();
        for (DeviceActionProto deviceActionProto : deviceActionsProto) {
            DeviceActionType actionType = toDeviceActionType(deviceActionProto.getType());
            deviceActionList.add(new DeviceAction(
                    deviceActionProto.getSensorId(),
                    actionType,
                    deviceActionProto.getValue())
            );
        }
        return deviceActionList;
    }

    protected DeviceActionType toDeviceActionType(ActionTypeProto actionTypeProto) {
        switch (actionTypeProto) {
            case ACTIVATE -> {
                return DeviceActionType.ACTIVATE;
            }
            case DEACTIVATE -> {
                return DeviceActionType.DEACTIVATE;
            }
            case INVERSE -> {
                return DeviceActionType.INVERSE;
            }
            case SET_VALUE -> {
                return DeviceActionType.SET_VALUE;
            }
            default -> {
                return null;
            }
        }
    }
}
