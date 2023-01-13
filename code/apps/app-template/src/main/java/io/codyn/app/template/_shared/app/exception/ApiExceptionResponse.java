package io.codyn.app.template._shared.app.exception;

import io.swagger.v3.oas.annotations.media.Schema;

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
        @Schema(description = "Additional, contextualized info for debugging mostly")
        String message) {

    public ApiExceptionResponse(Throwable exception) {
        this(exception.getClass().getSimpleName(), exception.getMessage());
    }
}
