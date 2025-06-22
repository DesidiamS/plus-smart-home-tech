package ru.practicum.collector.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.sensor.SensorEvent;
import ru.practicum.collector.service.KafkaHubEventProducer;
import ru.practicum.collector.service.KafkaSensorEventProducer;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventController {

    private final KafkaHubEventProducer hubEventProducer;
    private final KafkaSensorEventProducer sensorEventProducer;

    @PostMapping("/sensors")
    public void sensors(@RequestBody SensorEvent sensorEvent) {
        sensorEventProducer.send(sensorEvent);
    }

    @PostMapping("/hubs")
    public void hubs(@RequestBody HubEvent hubEvent) {
        hubEventProducer.send(hubEvent);
    }

}
