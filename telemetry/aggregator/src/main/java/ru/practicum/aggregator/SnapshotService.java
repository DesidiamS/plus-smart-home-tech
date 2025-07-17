package ru.practicum.aggregator;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class SnapshotService {

    Map<String, SensorSnapshotAvro> snapshotAvroMap = new HashMap<>();

    public SensorSnapshotAvro getSnapshotAvro(SensorEventAvro sensorEventAvro) {
        String hubId = sensorEventAvro.getHubId();

        if (snapshotAvroMap.containsKey(hubId)) {
            SensorSnapshotAvro oldSnapshot = snapshotAvroMap.get(hubId);
            SensorSnapshotAvro snapshot = updateSnapshot(oldSnapshot, sensorEventAvro);

            if (snapshot != null) {
                snapshotAvroMap.put(hubId, snapshot);
            }

            return snapshot;
        } else {
            SensorSnapshotAvro snapshot = createSnapshot(sensorEventAvro);
            snapshotAvroMap.put(hubId, snapshot);

            return snapshot;
        }
    }

    protected SensorSnapshotAvro createSnapshot(SensorEventAvro sensorEventAvro) {
        Map<String, SensorStateAvro> stateMap = new HashMap<>();

        SensorStateAvro sensorStateAvro = SensorStateAvro.newBuilder()
                .setTimestamp(sensorEventAvro.getTimestamp())
                .setState(sensorEventAvro.getPayload())
                .build();

        sensorStateAvro.put(sensorEventAvro.getId(), sensorStateAvro);

        return SensorSnapshotAvro.newBuilder()
                .setHubId(sensorEventAvro.getHubId())
                .setTimestamp(Instant.now())
                .setSensorStateList(stateMap)
                .build();
    }

    protected SensorSnapshotAvro updateSnapshot(SensorSnapshotAvro oldSnapshot, SensorEventAvro sensorEventAvro) {
        if (!oldSnapshot.getSensorStateList().containsKey(sensorEventAvro.getId()) &&
                !oldSnapshot.getSensorStateList().get(sensorEventAvro.getId()).getState().equals(sensorEventAvro.getPayload())) {

            SensorStateAvro sensorStateAvro = SensorStateAvro.newBuilder()
                    .setTimestamp(sensorEventAvro.getTimestamp())
                    .setState(sensorEventAvro.getPayload())
                    .build();

            oldSnapshot.getSensorStateList().put(sensorEventAvro.getId(), sensorStateAvro);
            oldSnapshot.setTimestamp(Instant.now());

            return oldSnapshot;
        } else {
            return null;
        }
    }
}
