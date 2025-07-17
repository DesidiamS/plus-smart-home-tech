package ru.practicum.aggregator;

import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.VoidSerializer;
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
public class KafkaSensorSnapshot {

    private final SnapshotService snapshotService;
    Consumer<String, SpecificRecordBase> consumer;
    Producer<String, SpecificRecordBase> producer;

    public void start() {
        try {
            consumer = new KafkaConsumer<>(getConsumerProperties());
            producer = new KafkaProducer<>(getProducerProperties());

            consumer.subscribe(List.of("telemetry.sensors.v1"));

            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorEventAvro event = (SensorEventAvro) record.value();
                    Optional<SensorSnapshotAvro> snapshot = Optional.ofNullable(snapshotService.getSnapshotAvro(event));
                    if (snapshot.isPresent()) {
                        ProducerRecord<String, SpecificRecordBase> message = new ProducerRecord<>("telemetry.snapshots.v1",
                                null, event.getTimestamp().toEpochMilli(), event.getHubId(), snapshot.get());

                        producer.send(message);
                    }
                }
                consumer.commitSync();
            }
        } catch (Exception ignored) {
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                consumer.close();
                producer.close();
            }
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
