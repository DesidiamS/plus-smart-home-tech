package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.domain.Action;
import ru.yandex.practicum.domain.Condition;
import ru.yandex.practicum.domain.Scenario;
import ru.yandex.practicum.domain.Sensor;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScenarioAddedService implements HubEventService {

    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Override
    @Transactional
    public void save(HubEventAvro event) {
        ScenarioAddedEventAvro scenarioAddedEventAvro = (ScenarioAddedEventAvro) event.getPayload();
        Scenario scenario = scenarioRepository.findByHubIdAndName(event.getHubId(),
                scenarioAddedEventAvro.getName()).orElseGet(() -> scenarioRepository.save(new Scenario(event.getHubId(),
                scenarioAddedEventAvro.getName())));

        saveConditions(scenario, scenarioAddedEventAvro, event.getHubId());
        saveActions(scenario, scenarioAddedEventAvro, event.getHubId());
    }

    private void saveConditions(Scenario scenario, ScenarioAddedEventAvro scenarioAddedEventAvro, String hubId) {
        Collection<String> sensorIds = scenarioAddedEventAvro.getConditions().stream()
                .map(ScenarioConditionAvro::getSensorId)
                .toList();

        if (sensorRepository.existsByIdInAndHubId(sensorIds, hubId)) {
            List<Condition> conditions = new ArrayList<>();
            for (ScenarioConditionAvro conditionAvro : scenarioAddedEventAvro.getConditions()) {
                Sensor sensor = sensorRepository.findById(conditionAvro.getSensorId()).orElseThrow();
                Condition condition = new Condition(
                        Long.valueOf(sensor.getId()),
                        conditionAvro.getType(),
                        conditionAvro.getOperation(),
                        scenario,
                        sensor,
                        (Integer) conditionAvro.getValue()
                );

                conditions.add(condition);
            }

            conditionRepository.saveAll(conditions);
        }
    }

    private void saveActions(Scenario scenario, ScenarioAddedEventAvro scenarioAddedEventAvro, String hubId) {
        Collection<String> sensorIds = scenarioAddedEventAvro.getActions().stream()
                .map(DeviceActionAvro::getSensorId)
                .toList();

        if (sensorRepository.existsByIdInAndHubId(sensorIds, hubId)) {
            List<Action> actions = new ArrayList<>();
            for (DeviceActionAvro deviceActionAvro : scenarioAddedEventAvro.getActions()) {
                Sensor sensor = sensorRepository.findById(deviceActionAvro.getSensorId()).orElseThrow();
                Action action = new Action(
                        Long.valueOf(sensor.getId()),
                        deviceActionAvro.getType(),
                        deviceActionAvro.getValue(),
                        scenario,
                        sensor
                );

                actions.add(action);
            }

            actionRepository.saveAll(actions);
        }
    }

    @Override
    public String getEventType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }
}
