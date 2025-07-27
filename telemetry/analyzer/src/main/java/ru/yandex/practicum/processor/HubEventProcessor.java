package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.deserializer.HubEventAvroDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.HubEvent;
import ru.yandex.practicum.service.HubEventService;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    @Value("${kafka.hub-event.topic}")
    private String topic;

    private final HubEvent hubEvent;

    @Override
    public void run() {
        try (KafkaConsumer<String, HubEventAvro> consumer = new KafkaConsumer<>(getConsumerProperties())) {
            consumer.subscribe(List.of(topic));

            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            Map<String, HubEventService> hubEventServices = hubEvent.getServices();

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(10000));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro hubEventAvro = record.value();

                    String payload = hubEventAvro.getClass().getSimpleName();

                    if (hubEventServices.containsKey(payload)) {
                        hubEventServices.get(payload).save(hubEventAvro);
                    }
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
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventAvroDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "hubEvent.group.id");
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "HubEventConsumer");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        return properties;
    }
}
