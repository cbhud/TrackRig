package me.cbhud.trackRig.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        String status,
        String message,
        LocalDateTime timestamp,
        Map<String, String> fieldErrors
) {
    public ApiErrorResponse(String status, String message, LocalDateTime timestamp) {
        this(status, message, timestamp, null);
    }
}