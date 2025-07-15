package ru.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.springframework.stereotype.Component;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.mapper.HubEventMapper;
import ru.practicum.serializer.HubEventAvroSerializer;

import java.util.Properties;

@Component
@RequiredArgsConstructor
public class KafkaHubEventProducer {

    private final HubEventMapper mapper;

    public void send(HubEvent hubEvent) {
        Properties config = new Properties();
        config.put("bootstrap.servers", "localhost:9092");

        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, HubEventAvroSerializer.class);

        String topic = "telemetry.hubs.v1";

        SpecificRecordBase serializedData = mapper.toHubEventAvro(hubEvent);

        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(topic, serializedData);

        try (Producer<String, SpecificRecordBase> producer = new KafkaProducer<>(config)) {
            producer.send(producerRecord);
        }
    }
}
