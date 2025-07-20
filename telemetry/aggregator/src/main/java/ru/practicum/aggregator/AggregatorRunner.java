package ru.practicum.aggregator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AggregatorRunner implements CommandLineRunner {

    private final KafkaSensorSnapshot kafkaSensorSnapshot;

    @Override
    public void run(String... args) throws Exception {
        kafkaSensorSnapshot.run();
    }
}
