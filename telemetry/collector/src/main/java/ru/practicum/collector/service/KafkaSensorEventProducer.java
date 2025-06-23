package ru.practicum.collector.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.springframework.stereotype.Component;
import ru.practicum.collector.model.sensor.SensorEvent;

import java.util.Properties;

@Component
public class KafkaSensorEventProducer {

    public void send(SensorEvent sensorEvent) {
        Properties config = new Properties();
        config.put("bootstrap.servers", "localhost:9092");

        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SensorEventAvroSerializer.class);

        String topic = "telemetry.sensors.v1";

        ProducerRecord<String, SensorEvent> producerRecord = new ProducerRecord<>(topic, sensorEvent);

        try (Producer<String, SensorEvent> producer = new KafkaProducer<>(config)) {
            producer.send(producerRecord);
        }
    }
}
