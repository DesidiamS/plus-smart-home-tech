package ru.practicum.aggregator;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SnapshotService {

    Map<String, SensorSnapshotAvro> snapshotAvroMap = new HashMap<>();

    public Optional<SensorSnapshotAvro> getSnapshotAvro(SensorEventAvro sensorEventAvro) {
        String hubId = sensorEventAvro.getHubId();

        if (snapshotAvroMap.containsKey(hubId)) {
            SensorSnapshotAvro oldSnapshot = snapshotAvroMap.get(hubId);
            Optional<SensorSnapshotAvro> snapshot = updateSnapshot(oldSnapshot, sensorEventAvro);

            snapshot.ifPresent(s -> snapshotAvroMap.put(hubId, s));
            return snapshot;
        } else {
            SensorSnapshotAvro snapshot = createSnapshot(sensorEventAvro);
            snapshotAvroMap.put(hubId, snapshot);

            return Optional.of(snapshot);
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

    protected Optional<SensorSnapshotAvro> updateSnapshot(SensorSnapshotAvro oldSnapshot, SensorEventAvro sensorEventAvro) {
        String sensorId = sensorEventAvro.getId();
        if (!oldSnapshot.getSensorStateList().containsKey(sensorId)) {
            if (oldSnapshot.getSensorStateList().get(sensorId).getTimestamp().isAfter(sensorEventAvro.getTimestamp()) ||
            !oldSnapshot.getSensorStateList().get(sensorId).getState().equals(sensorEventAvro.getPayload())) {
                return  Optional.empty();
            }
        }

        SensorStateAvro sensorStateAvro = SensorStateAvro.newBuilder()
                .setTimestamp(sensorEventAvro.getTimestamp())
                .setState(sensorEventAvro.getPayload())
                .build();

        oldSnapshot.getSensorStateList().put(sensorEventAvro.getId(), sensorStateAvro);
        oldSnapshot.setTimestamp(Instant.now());

        return Optional.of(oldSnapshot);
    }
}
