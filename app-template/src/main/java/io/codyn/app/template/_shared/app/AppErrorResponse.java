package io.codyn.app.template._shared.app;

import io.codyn.app.template._shared.domain.exception.AppException;

import java.util.List;

public record AppErrorResponse(String exception, String message, List<String> reasons) {

    public AppErrorResponse(Throwable exception, String... reasons) {
        this(exception, List.of(reasons));
    }

    public AppErrorResponse(Throwable exception, List<String> reasons) {
        this(exception.getClass().getSimpleName(), exception.getMessage(), reasons);
    }

    public AppErrorResponse(AppException exception) {
        this(exception, exception.reasons());
    }
}
