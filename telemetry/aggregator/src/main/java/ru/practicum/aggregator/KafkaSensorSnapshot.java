package ru.practicum.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import ru.practicum.deserializer.SensorEventAvroDeserializer;
import ru.practicum.serializer.SensorEventAvroSerializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaSensorSnapshot {

    private final SnapshotService snapshotService;

    public void run() {
        log.info("Starting Kafka Sensor Snapshot");
        try (KafkaConsumer<String, SpecificRecordBase> consumer = new KafkaConsumer<>(getConsumerProperties());
             KafkaProducer<String, SpecificRecordBase> producer = new KafkaProducer<>(getProducerProperties())) {
            consumer.subscribe(List.of("telemetry.sensors.v1"));

            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (!Thread.currentThread().isInterrupted()) {
                log.info("Обработка данных");
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(10000));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorEventAvro event = (SensorEventAvro) record.value();
                    Optional<SensorSnapshotAvro> snapshot = snapshotService.getSnapshotAvro(event);
                    log.info("Получение снимка {}", snapshot);
                    if (snapshot.isPresent()) {
                        log.info("Запись снимка в kafka");
                        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(
                                "telemetry.snapshots.v1", null, event.getTimestamp().getEpochSecond(),
                                event.getHubId(), snapshot.get());

                        producer.send(producerRecord);
                    }
                }
                consumer.commitSync();
            }
        } catch (Exception e) {
            log.error("Kafka Sensor Snapshot error", e);
        }
    }

    private static Properties getConsumerProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventAvroDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "sensorEvent.group.id");
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "SensorEventConsumer");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        return properties;
    }

    private static Properties getProducerProperties() {
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SensorEventAvroSerializer.class);

        return properties;
    }
}