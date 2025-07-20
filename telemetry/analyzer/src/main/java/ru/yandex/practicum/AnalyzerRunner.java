package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.processor.HubEventProcessor;
import ru.yandex.practicum.processor.SnapshotProcessor;

import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
public class AnalyzerRunner implements CommandLineRunner {

    private final HubEventProcessor hubEventProcessor;
    private final SnapshotProcessor snapshotProcessor;
    private final ExecutorService executorservice;

    public void run(String... args) {
        executorservice.submit(hubEventProcessor);
        executorservice.submit(snapshotProcessor);
    }
}
