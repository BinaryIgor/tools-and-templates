package io.codyn.app.template._shared.app.exception;

import io.codyn.app.template._shared.domain.exception.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@ApiResponses(
        value = {
                @ApiResponse(responseCode = "400", description = "Something about the request is invalid"),
                @ApiResponse(responseCode = "401", description = "We don't know who you are, or token has expired/was invalid"),
                @ApiResponse(responseCode = "403", description = "Given resource is not available for a user"),
                @ApiResponse(responseCode = "404", description = "Given resource can't be found"),
                @ApiResponse(responseCode = "409", description = "Given resource exist or there was an optimist lock exception")
        }
)
public class ApiExceptionsHandler {


    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ApiExceptionResponse> handleException(UnauthenticatedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiExceptionResponse(exception));
    }

    @ExceptionHandler(InvalidAuthTokenException.class)
    public ResponseEntity<ApiExceptionResponse> handleException(InvalidAuthTokenException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiExceptionResponse(exception));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiExceptionResponse> handleException(ConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiExceptionResponse(exception));
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ApiExceptionResponse> handleException(OptimisticLockException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiExceptionResponse(exception));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiExceptionResponse> handleException(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiExceptionResponse(exception));
    }

    @ExceptionHandler(AccessForbiddenException.class)
    public ResponseEntity<ApiExceptionResponse> handleException(AccessForbiddenException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiExceptionResponse(exception));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiExceptionResponse> handleException(CustomException exception) {
        return badRequestResponse(exception);
    }

    private ResponseEntity<ApiExceptionResponse> badRequestResponse(Throwable exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiExceptionResponse(exception));
    }

    @ExceptionHandler(MissingRequestValueException.class)
    public ResponseEntity<ApiExceptionResponse> handleException(MissingRequestValueException exception) {
        return badRequestResponse(exception);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiExceptionResponse> handleException(HttpMessageNotReadableException exception) {
        return badRequestResponse(exception);
    }
}
