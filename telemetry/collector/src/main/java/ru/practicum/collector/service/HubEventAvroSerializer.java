package ru.practicum.collector.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
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
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class HubEventAvroSerializer implements Serializer<HubEvent> {

    private final EncoderFactory encoderFactory = EncoderFactory.get();

    @Override
    public byte[] serialize(String topic, HubEvent data) {
        if (data == null) {
            return null;
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BinaryEncoder encoder = encoderFactory.binaryEncoder(outputStream, null);
            DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(getSchemaForEvent(data));
            SpecificRecordBase record = hubToAvro(data);
            writer.write(record, encoder);
            encoder.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    private Schema getSchemaForEvent(HubEvent event) {
        return switch (event.getType()) {
            case DEVICE_ADDED -> DeviceAddedEventAvro.getClassSchema();
            case DEVICE_REMOVED -> DeviceRemovedEventAvro.getClassSchema();
            case SCENARIO_ADDED -> ScenarioAddedEventAvro.getClassSchema();
            case SCENARIO_REMOVED -> ScenarioRemovedEventAvro.getClassSchema();
        };
    }

    private SpecificRecordBase hubToAvro(HubEvent data) {
        HubEventAvro hubEvent = new HubEventAvro();
        hubEvent.setHubId(data.getHubId());
        hubEvent.setTimestamp(data.getTimestamp());

        return switch (data.getType()) {
            case DEVICE_ADDED -> {
                DeviceAddedEvent deviceAddedEvent = (DeviceAddedEvent) data;
                yield DeviceAddedEventAvro.newBuilder()
                        .setId(deviceAddedEvent.getId())
                        .setType(DeviceTypeAvro.valueOf(deviceAddedEvent.getDeviceType().toString()))
                        .build();
            }
            case DEVICE_REMOVED -> {
                DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) data;
                yield DeviceRemovedEventAvro.newBuilder()
                        .setId(deviceRemovedEvent.getId())
                        .build();
            }
            case SCENARIO_ADDED -> {
                ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) data;
                yield ScenarioAddedEventAvro.newBuilder()
                        .setConditions(convertConditionsToAvro(scenarioAddedEvent.getConditions()))
                        .setActions(convertActionsToAvro(scenarioAddedEvent.getActions()))
                        .setName(scenarioAddedEvent.getName())
                        .build();
            }
            case SCENARIO_REMOVED -> {
                ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) data;
                yield ScenarioRemovedEventAvro.newBuilder()
                        .setName(scenarioRemovedEvent.getName())
                        .build();
            }
        };
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
