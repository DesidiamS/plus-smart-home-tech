package ru.practicum.collector.grpc;


import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.collector.grpc.hub.HubBuilder;
import ru.practicum.collector.grpc.sensor.SensorBuilder;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@GrpcService
public class CollectorController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<SensorEventProto.PayloadCase, SensorBuilder> sensorBuilder;
    private final Map<HubEventProto.PayloadCase, HubBuilder> hubBuilder;

    public CollectorController(Set<SensorBuilder> sensorBuilder, Set<HubBuilder> hubBuilder) {
        this.sensorBuilder = sensorBuilder.stream()
                .collect(Collectors.toMap(
                        SensorBuilder::getPayloadCase,
                        Function.identity()
                ));
        this.hubBuilder = hubBuilder.stream()
                .collect(Collectors.toMap(
                        HubBuilder::getPayloadCase,
                        Function.identity()
                ));
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            sensorBuilder.get(request.getPayloadCase()).build(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.fromThrowable(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            if (hubBuilder.containsKey(request.getPayloadCase())) {
                hubBuilder.get(request.getPayloadCase()).build(request);
            } else {
                throw new IllegalArgumentException();
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.fromThrowable(e)
            ));
        }
    }
}
