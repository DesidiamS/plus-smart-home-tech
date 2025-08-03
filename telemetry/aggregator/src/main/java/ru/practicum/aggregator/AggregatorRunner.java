package ru.practicum.aggregator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AggregatorRunner implements ApplicationRunner {

    private final KafkaSensorSnapshot kafkaSensorSnapshot;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread snapshotProcessorThread = new Thread(kafkaSensorSnapshot);
        snapshotProcessorThread.setName("KafkaSensorSnapshot");
        snapshotProcessorThread.start();
    }
}
