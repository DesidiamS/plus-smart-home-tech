package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.processor.HubEventProcessor;
import ru.yandex.practicum.processor.SnapshotProcessor;

@Component
@RequiredArgsConstructor
public class AnalyzerRunner implements Runnable {

    private final HubEventProcessor hubEventProcessor;
    private final SnapshotProcessor snapshotProcessor;

    @Override
    public void run() {
        Thread thread = new Thread(hubEventProcessor);
        thread.setName("HUB PROCESSOR");
        thread.start();

        snapshotProcessor.run();
    }
}
