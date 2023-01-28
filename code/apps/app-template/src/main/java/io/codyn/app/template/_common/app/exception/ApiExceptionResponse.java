package io.codyn.app.template._common.app.exception;

import io.codyn.app.template._common.core.exception.AppException;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ApiExceptionResponse(
        @Schema(description = "One of the api exceptions. Details are documented under: /docs/errors")
        String error,
        @Schema(description = "Optional list of reasons codes to further interpret given exception type. Details are documented under: /docs/errors")
        List<String> reasons,
        @Schema(description = "Additional, contextualized info for debugging mostly")
        String message) {

    public ApiExceptionResponse(Throwable exception) {
        this(exception.getClass().getSimpleName(), List.of(), exception.getMessage());
    }

    public ApiExceptionResponse(AppException exception) {
        this(exception.toError(), exception.reasons(), exception.getMessage());
    }
}
