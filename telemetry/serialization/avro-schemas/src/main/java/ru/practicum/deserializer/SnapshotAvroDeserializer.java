package ru.practicum.deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;

public class SnapshotAvroDeserializer extends AbstractAvroDeserializer<SensorSnapshotAvro> {
    public SnapshotAvroDeserializer() {
        super(SensorSnapshotAvro.getClassSchema());
    }
}
