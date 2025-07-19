package ru.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import ru.practicum.collector.model.mapper.SensorEventMapper;
import ru.practicum.collector.model.sensor.SensorEvent;
import ru.practicum.serializer.SensorEventAvroSerializer;

import java.util.Properties;

@Component
@RequiredArgsConstructor
public class KafkaSensorEventProducer {

    private final SensorEventMapper sensorEventMapper;

    public void send(SensorEvent sensorEvent) {
        Properties config = new Properties();
        config.put("bootstrap.servers", "localhost:9092");

        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SensorEventAvroSerializer.class);

        String topic = "telemetry.sensors.v1";

        SpecificRecordBase serializedData = sensorEventMapper.toSensorEventAvro(sensorEvent);

        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(topic, serializedData);

        try (Producer<String, SpecificRecordBase> producer = new KafkaProducer<>(config)) {
            producer.send(producerRecord);
        }
    }
}
