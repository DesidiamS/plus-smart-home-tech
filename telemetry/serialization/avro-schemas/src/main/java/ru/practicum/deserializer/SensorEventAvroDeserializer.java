package ru.practicum.deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public class SensorEventAvroDeserializer extends AbstractAvroDeserializer<SensorEventAvro> {

    public SensorEventAvroDeserializer() {
        super(SensorEventAvro.getClassSchema());
    }
}
