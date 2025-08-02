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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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

    @Value("${consumer.sensor-topic}")
    private String sensorTopic;

    @Value("${consumer.snapshot-topic}")
    private String snapshotTopic;

    @Value("${spring.kafka.bootstrap-servers}")
    private static String bootstrapServer;

    @Value("${spring.kafka.producer.key-serializer}")
    private static String producerKeySerializer;

    @Value("${spring.kafka.producer.value-serializer}")
    private static String producerValueSerializer;

    @Value("${spring.kafka.consumer.key-deserializer}")
    private static String consumerKeyDeserializer;

    @Value("${spring.kafka.consumer.value-deserializer}")
    private static String consumerValueDeserializer;

    @Value("${spring.kafka.consumer.group-id}")
    private static String consumerGroupId;

    @Value("${spring.kafka.consumer.client-id}")
    private static String consumerClientId;

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private static String consumerEnableAutoCommit;

    public void run() {
        log.info("Starting Kafka Sensor Snapshot");
        try (KafkaConsumer<String, SpecificRecordBase> consumer = new KafkaConsumer<>(getConsumerProperties());
             KafkaProducer<String, SpecificRecordBase> producer = new KafkaProducer<>(getProducerProperties())) {
            consumer.subscribe(List.of(sensorTopic));

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
                                snapshotTopic, null, event.getTimestamp().getEpochSecond(),
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
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerKeyDeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerValueDeserializer);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, consumerClientId);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerEnableAutoCommit);

        return properties;
    }

    private static Properties getProducerProperties() {
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerKeySerializer);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerValueSerializer);

        return properties;
    }
}