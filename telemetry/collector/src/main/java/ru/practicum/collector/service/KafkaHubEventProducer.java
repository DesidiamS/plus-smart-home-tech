package ru.practicum.collector.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.springframework.stereotype.Component;
import ru.practicum.collector.model.hub.HubEvent;

import java.util.Properties;

@Component
public class KafkaHubEventProducer {

    public void send(HubEvent hubEvent) {
        Properties config = new Properties();
        config.put("bootstrap.servers", "localhost:9092");

        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, HubEventAvroSerializer.class);

        String topic = "telemetry.hubs.v1";

        ProducerRecord<String, HubEvent> producerRecord = new ProducerRecord<>(topic, hubEvent);

        try (Producer<String, HubEvent> producer = new KafkaProducer<>(config)) {
            producer.send(producerRecord);
        }
    }
}
