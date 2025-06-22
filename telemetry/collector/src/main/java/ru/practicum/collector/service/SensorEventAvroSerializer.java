package ru.practicum.collector.service;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Serializer;
import ru.practicum.collector.model.sensor.ClimateSensorEvent;
import ru.practicum.collector.model.sensor.LightSensorEvent;
import ru.practicum.collector.model.sensor.MotionSensorEvent;
import ru.practicum.collector.model.sensor.SensorEvent;
import ru.practicum.collector.model.sensor.SwitchSensorEvent;
import ru.practicum.collector.model.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SensorEventAvroSerializer implements Serializer<SensorEvent> {

    private final EncoderFactory encoderFactory = EncoderFactory.get();

    @Override
    public byte[] serialize(String topic, SensorEvent data) {
        if (data == null) {
            return null;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BinaryEncoder encoder = encoderFactory.binaryEncoder(outputStream, null);

            SpecificDatumWriter<SpecificRecord> datumWriter = new SpecificDatumWriter<>(getSchemaForEvent(data));

            SpecificRecord record = sensorToAvro(data);

            datumWriter.write(record, encoder);

            encoder.flush();

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Schema getSchemaForEvent(SensorEvent event) {
        return switch (event.getType()) {
            case CLIMATE_SENSOR -> ClimateSensorAvro.getClassSchema();
            case LIGHT_SENSOR -> LightSensorAvro.getClassSchema();
            case MOTION_SENSOR -> MotionSensorAvro.getClassSchema();
            case SWITCH_SENSOR -> SwitchSensorAvro.getClassSchema();
            case TEMPERATURE_SENSOR -> TemperatureSensorAvro.getClassSchema();
        };
    }

    private SpecificRecord sensorToAvro(SensorEvent data) {
        SensorEventAvro sensorEvent = new SensorEventAvro();
        sensorEvent.setHubId(data.getHubId());
        sensorEvent.setTimestamp(data.getTimestamp());

        return switch (data.getType()) {
            case CLIMATE_SENSOR -> {
                ClimateSensorEvent climateSensorEvent = (ClimateSensorEvent) data;
                yield ClimateSensorAvro.newBuilder()
                        .setCo2Level(climateSensorEvent.getCo2Level())
                        .setHumidity(climateSensorEvent.getHumidity())
                        .setTemperatureC(climateSensorEvent.getTemperatureC())
                        .build();
            }
            case LIGHT_SENSOR -> {
                LightSensorEvent lightSensorEvent = (LightSensorEvent) data;
                yield LightSensorAvro.newBuilder()
                        .setLinkQuality(lightSensorEvent.getLinkQuality())
                        .setLuminosity(lightSensorEvent.getLuminosity())
                        .build();

            }
            case MOTION_SENSOR -> {
                MotionSensorEvent motionSensorEvent = (MotionSensorEvent) data;
                yield MotionSensorAvro.newBuilder()
                        .setLinkQuality(motionSensorEvent.getLinkQuantity())
                        .setMotion(motionSensorEvent.getMotion())
                        .setVoltage(motionSensorEvent.getVoltage())
                        .build();
            }
            case SWITCH_SENSOR -> {
                SwitchSensorEvent switchSensorEvent = (SwitchSensorEvent) data;
                yield SwitchSensorAvro.newBuilder()
                        .setState(switchSensorEvent.getState())
                        .build();
            }
            case TEMPERATURE_SENSOR -> {
                TemperatureSensorEvent temperatureSensorEvent = (TemperatureSensorEvent) data;
                yield TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(temperatureSensorEvent.getTemperatureC())
                        .setTemperatureF(temperatureSensorEvent.getTemperatureF())
                        .setId(temperatureSensorEvent.getId())
                        .setHubId(temperatureSensorEvent.getHubId())
                        .setTimestamp(temperatureSensorEvent.getTimestamp())
                        .build();
            }
        };
    }
}
