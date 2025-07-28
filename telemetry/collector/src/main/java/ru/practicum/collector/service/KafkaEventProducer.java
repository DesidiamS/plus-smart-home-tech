package ru.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.practicum.serializer.CollectorAvroSerializer;

import java.util.Properties;

@Component
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final Producer<String, SpecificRecordBase> producer;

    public KafkaEventProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CollectorAvroSerializer.class);
        config.put(ProducerConfig.CLIENT_ID_CONFIG, "telemetry.collector");
        this.producer = new KafkaProducer<>(config);
    }

    public void send(SpecificRecordBase message, String topic) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, message);
        producer.send(record);
        producer.flush();
    }
}
