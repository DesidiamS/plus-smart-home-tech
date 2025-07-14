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
            SensorStateAvro sensorStateAvro = SensorStateAvro.newBuilder()
                    .setTimestamp(sensorEventAvro.getTimestamp())
                    .setState(sensorEventAvro.getPayload())
                    .build();

            Map<String, SensorStateAvro> stateMap = new HashMap<>();

            stateMap.put(sensorEventAvro.getId(), sensorStateAvro);

            return SensorSnapshotAvro.newBuilder()
                    .setHubId(sensorEventAvro.getHubId())
                    .setTimestamp(Instant.now())
                    .setSensorStateList(stateMap).build();
        } else {
            SensorSnapshotAvro sensorSnapshot = snapshotAvroMap.get(hubId);

            String sensorId = sensorEventAvro.getId();
            Map<String, SensorStateAvro> sensorsState = sensorSnapshot.getSensorStateList();

            if (sensorsState.containsKey(sensorId) &&
                    (sensorsState.get(sensorId).getTimestamp().isAfter(sensorEventAvro.getTimestamp()) ||
                            sensorsState.get(sensorId).getState().equals(sensorEventAvro.getPayload())
                    )) {
                return null;
            }

            SensorStateAvro newState = SensorStateAvro.newBuilder()
                    .setTimestamp(sensorEventAvro.getTimestamp())
                    .setState(sensorEventAvro.getPayload())
                    .build();

            sensorSnapshot.getSensorStateList().put(sensorId, newState);
            sensorSnapshot.setTimestamp(sensorEventAvro.getTimestamp());

            return sensorSnapshot;
        }
    }
}
