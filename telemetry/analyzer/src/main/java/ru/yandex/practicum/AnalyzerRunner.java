package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.processor.HubEventProcessor;
import ru.yandex.practicum.processor.SnapshotProcessor;

@Component
@RequiredArgsConstructor
public class AnalyzerRunner implements ApplicationRunner {

    private final HubEventProcessor hubEventProcessor;
    private final SnapshotProcessor snapshotProcessor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread hubEventProcessorThread = new Thread(hubEventProcessor);
        hubEventProcessorThread.setName("HubEventProcessor");
        hubEventProcessorThread.start();

        Thread snapshotProcessorThread = new Thread(snapshotProcessor);
        snapshotProcessorThread.setName("SnapshotProcessor");
        snapshotProcessorThread.start();
    }
}
