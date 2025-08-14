package ru.yandex.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import ru.yandex.practicum.exception.BadRequestException;
import ru.yandex.practicum.exception.ErrorResponse;

import java.io.IOException;
import java.io.InputStream;

public class WarehouseErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {

        ErrorResponse errorResponse;

        try (InputStream body = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            errorResponse = mapper.readValue(body, ErrorResponse.class);
        } catch (IOException ex) {
            return new Exception(ex.getMessage());
        }

        if (response.status() == 400) {
            return new BadRequestException(errorResponse.getMessage() != null ? errorResponse.getMessage() : s);
        } else {
            return defaultErrorDecoder.decode(s, response);
        }
    }
}
