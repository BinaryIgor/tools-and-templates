package io.codyn.app.template._shared.app.exception;

import io.codyn.app.template._shared.domain.exception.AppException;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ApiExceptionResponse(
        @Schema(description = """
                On of the app exceptions. Some of the possible values to handle
                (more are possible, but they should never occur during proper API usage!):
                * EmailTakenException
                * EmailNotReachableException
                * InvalidAuthTokenException
                * OptimisticLockException
                * ResourceExistsException
                * ResourceForbidden
                * ValidationException
                * UnauthenticatedException
                * InvalidActivationTokenException
                """)
        String exception,
        @Schema(description = "Optional list of reasons codes to further interpret given exception type")
        List<String> reasons,
        @Schema(description = "Additional, contextualized info for debugging mostly")
        String message) {

    public ApiExceptionResponse(Throwable exception) {
        this(exception.getClass().getSimpleName(),
                (exception instanceof AppException ae) ? ae.reasons : List.of(),
                exception.getMessage());
    }
}
