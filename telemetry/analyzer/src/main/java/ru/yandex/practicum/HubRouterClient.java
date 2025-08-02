package ru.yandex.practicum;

import com.google.protobuf.Timestamp;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.domain.Action;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

import java.time.Instant;

@Service
public class HubRouterClient {

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubClient;

    public HubRouterClient(@GrpcClient("hub-router")
                           HubRouterControllerGrpc.HubRouterControllerBlockingStub hubClient) {
        this.hubClient = hubClient;
    }

    public void sendAction(Action action) {
        ActionTypeProto actionTypeProto = null;
        switch (action.getType()) {
            case ACTIVATE -> actionTypeProto = ActionTypeProto.ACTIVATE;
            case DEACTIVATE -> actionTypeProto = ActionTypeProto.DEACTIVATE;
            case INVERSE -> actionTypeProto = ActionTypeProto.INVERSE;
            case SET_VALUE -> actionTypeProto = ActionTypeProto.SET_VALUE;
        }

        DeviceActionProto deviceActionProto = DeviceActionProto.newBuilder()
                .setSensorId(action.getSensor().getId())
                .setType(actionTypeProto)
                .setValue(action.getValue())
                .build();

        Timestamp timestamp = Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).setNanos(Instant.now().getNano()).build();

        DeviceActionRequest request = DeviceActionRequest.newBuilder()
                .setHubId(action.getScenario().getHubId())
                .setScenarioName(action.getScenario().getName())
                .setAction(deviceActionProto)
                .setTimestamp(timestamp)
                .build();

        hubClient.handleDeviceAction(request);
    }
}
