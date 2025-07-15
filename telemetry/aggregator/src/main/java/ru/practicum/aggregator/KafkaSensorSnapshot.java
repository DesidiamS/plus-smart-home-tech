package ru.practicum.aggregator;

import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import ru.practicum.deserializer.SensorEventAvroDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.springframework.stereotype.Component;
import ru.practicum.serializer.SensorEventAvroSerializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class KafkaSensorSnapshot {

    private final SnapshotService snapshotService;

    public void start() {
        try (KafkaConsumer<String, SpecificRecordBase> consumer = new KafkaConsumer<>(getConsumerProperties());
             KafkaProducer<String, SpecificRecordBase> producer = new KafkaProducer<>(getProducerProperties())) {
            consumer.subscribe(List.of("telemetry.sensor.v1"));

            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorEventAvro event = (SensorEventAvro) record.value();
                    SensorSnapshotAvro snapshot = snapshotService.getSnapshotAvro(event);

                    if (snapshot != null) {
                        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(
                                "telemetry.snapshots.v1", null, event.getTimestamp().getEpochSecond(),
                                event.getHubId(), snapshot);

                        producer.send(producerRecord);
                    }
                }
                consumer.commitSync(Duration.ofMillis(100));
            }
        } catch (Exception ignored) {
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

        properties.put("bootstrap.servers", "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SensorEventAvroSerializer.class);

        return properties;
    }
}
