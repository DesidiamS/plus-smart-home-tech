package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.HubRouterClient;
import ru.yandex.practicum.domain.Action;
import ru.yandex.practicum.domain.Condition;
import ru.yandex.practicum.domain.Scenario;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SnapshotService {

    private final ActionRepository actionRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final HubRouterClient hubRouterClient;

    public void send(SensorSnapshotAvro sensorSnapshotAvro) {
        Map<String, SensorStateAvro> sensorStateMap = sensorSnapshotAvro.getSensorStateList();

        List<Scenario> scenarios = scenarioRepository.findByHubId(sensorSnapshotAvro.getHubId());

        List<Condition> conditions = conditionRepository.findByScenarioIn(scenarios);

        for (Condition condition : conditions) {
            if (checkCondition(condition, sensorStateMap)) {
                List<Action> actionScenarios = actionRepository.findByScenarioIn(scenarios);
                for (Action action : actionScenarios) {
                    hubRouterClient.sendAction(action);
                }
            }
        }
    }

    private boolean checkCondition(Condition condition, Map<String, SensorStateAvro> sensorStateMap) {
        SensorStateAvro sensorStateAvro = sensorStateMap.get(condition.getSensor().getId());

        switch (condition.getType()) {
            case LUMINOSITY -> {
                LightSensorAvro lightSensor = (LightSensorAvro) sensorStateAvro.getState();
                return Boolean.TRUE.equals(checkConditionOperation(condition, lightSensor.getLuminosity()));
            }
            case TEMPERATURE -> {
                ClimateSensorAvro temperatureSensor = (ClimateSensorAvro) sensorStateAvro.getState();
                return Boolean.TRUE.equals(checkConditionOperation(condition, temperatureSensor.getTemperatureC()));
            }
            case MOTION -> {
                MotionSensorAvro motionSensor = (MotionSensorAvro) sensorStateAvro.getState();
                return Boolean.TRUE.equals(checkConditionOperation(condition, motionSensor.getMotion() ? 1 : 0));
            }
            case SWITCH -> {
                SwitchSensorAvro switchSensor = (SwitchSensorAvro) sensorStateAvro.getState();
                return Boolean.TRUE.equals(checkConditionOperation(condition, switchSensor.getState() ? 1 : 0));
            }
            case CO2LEVEL -> {
                ClimateSensorAvro climateSensor = (ClimateSensorAvro) sensorStateAvro.getState();
                return Boolean.TRUE.equals(checkConditionOperation(condition, climateSensor.getCo2Level()));
            }
            case HUMIDITY -> {
                ClimateSensorAvro climateSensor = (ClimateSensorAvro) sensorStateAvro.getState();
                return Boolean.TRUE.equals(checkConditionOperation(condition, climateSensor.getHumidity()));
            }
            case null -> {
                return false;
            }
        }
    }

    private Boolean checkConditionOperation(Condition condition, Integer currentValue) {
        ConditionOperationAvro conditionOperation = condition.getOperation();
        Integer targetValue = condition.getValue();

        switch (conditionOperation) {
            case EQUALS -> {
                return targetValue.equals(currentValue);
            }
            case LOWER_THAN -> {
                return currentValue < targetValue;
            }
            case GREATER_THAN -> {
                return currentValue > targetValue;
            }
            case null -> {
                return null;
            }
        }
    }
}
