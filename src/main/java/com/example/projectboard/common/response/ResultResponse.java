package com.example.projectboard.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ResultResponse<T> {

    private boolean success;
    private LocalDateTime responseTime;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T error;

    public ResultResponse(final boolean success,
                          final LocalDateTime responseTime,
                          final String message) {
        this.success = success;
        this.responseTime = responseTime;
        this.message = message;
        this.data = null;
        this.error = null;
    }

    public static <T> ResultResponse<T> success(final String message) {
        return success(message, null);
    }

    public static <T> ResultResponse<T> success(final String message,
                                                final T data) {
        return ResultResponse.<T>builder()
                .success(true)
                .responseTime(LocalDateTime.now())
                .message(message)
                .data(data)
                .error(null)
                .build();
    }

    public static <T> ResultResponse<T> fail(final String message) {
        return fail(message, null);
    }

    public static <T> ResultResponse<T> fail(final String message,
                                                final T error) {
        return ResultResponse.<T>builder()
                .success(false)
                .responseTime(LocalDateTime.now())
                .message(message)
                .data(null)
                .error(error)
                .build();
    }
}
