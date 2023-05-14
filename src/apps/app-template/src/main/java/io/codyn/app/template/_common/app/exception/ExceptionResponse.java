package io.codyn.app.template._common.app.exception;

import io.codyn.app.template._common.core.exception.AppException;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ExceptionResponse(
        @Schema(description = "One or more api errors. Details are documented under: /docs/errors")
        List<String> errors,
        @Schema(description = "Additional, contextualized info for debugging mostly")
        String message) {

    public ExceptionResponse(Throwable exception) {
        this(AppException.defaultErrors(exception.getClass()), exception.getMessage());
    }

    public ExceptionResponse(AppException exception) {
        this(exception.toErrors(), exception.getMessage());
    }
}
