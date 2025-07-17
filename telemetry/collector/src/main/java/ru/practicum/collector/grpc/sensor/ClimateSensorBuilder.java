package ru.practicum.collector.grpc.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.sensor.ClimateSensorEvent;
import ru.practicum.collector.model.sensor.SensorEvent;
import ru.practicum.collector.service.KafkaSensorEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Component
public class ClimateSensorBuilder extends AbstractSensorBuilder {

    public ClimateSensorBuilder(KafkaSensorEventProducer sensorEventProducer) {
        super(sensorEventProducer);
    }

    @Override
    public SensorEvent toSensorEvent(SensorEventProto sensorEvent) {
        ClimateSensorEvent climateSensorEvent = new ClimateSensorEvent(
                sensorEvent.getClimateSensor().getTemperatureC(),
                sensorEvent.getClimateSensor().getHumidity(),
                sensorEvent.getClimateSensor().getCo2Level()
        );
        climateSensorEvent.setId(sensorEvent.getId());
        climateSensorEvent.setHubId(sensorEvent.getHubId());
        climateSensorEvent.setTimestamp(Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(), sensorEvent.getTimestamp().getNanos()));
        return climateSensorEvent;
    }

    @Override
    public SensorEventProto.PayloadCase getPayloadCase() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }
}
