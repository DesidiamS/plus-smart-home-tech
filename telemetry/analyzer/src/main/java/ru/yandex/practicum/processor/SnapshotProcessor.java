package ru.yandex.practicum.processor;

import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.deserializer.SnapshotAvroDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.service.SnapshotService;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {

    @Value("${kafka.snapshot.topic}")
    private String topic;

    private final SnapshotService snapshotService;

    @Override
    public void run() {
        try (KafkaConsumer<String, SpecificRecordBase> consumer = new KafkaConsumer<>(getConsumerProperties())) {
            consumer.subscribe(Collections.singleton(topic));

            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorSnapshotAvro sensorSnapshotAvro = (SensorSnapshotAvro) record.value();

                    snapshotService.send(sensorSnapshotAvro);
                }
                consumer.commitSync();
            }
        } catch (Exception ignored) {

        }
    }

    private static Properties getConsumerProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SnapshotAvroDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "snapshot.group.id");
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "SnapshotsConsumer");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        return properties;
    }
}
