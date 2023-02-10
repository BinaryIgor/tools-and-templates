package io.codyn.app.template._common.app.exception;

import io.codyn.app.template._common.core.exception.*;
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
public class ExceptionsHandler {


    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ExceptionResponse> handleException(UnauthenticatedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse(exception));
    }

    @ExceptionHandler(InvalidAuthTokenException.class)
    public ResponseEntity<ExceptionResponse> handleException(InvalidAuthTokenException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse(exception));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionResponse> handleException(ConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ExceptionResponse(exception));
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ExceptionResponse> handleException(OptimisticLockException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ExceptionResponse(exception));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(exception));
    }

    @ExceptionHandler(AccessForbiddenException.class)
    public ResponseEntity<ExceptionResponse> handleException(AccessForbiddenException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ExceptionResponse(exception));
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ExceptionResponse> handleException(AppException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(exception));
    }

    @ExceptionHandler(MissingRequestValueException.class)
    public ResponseEntity<ExceptionResponse> handleException(MissingRequestValueException exception) {
        return badRequestResponse(exception);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleException(HttpMessageNotReadableException exception) {
        return badRequestResponse(exception);
    }

    private ResponseEntity<ExceptionResponse> badRequestResponse(Throwable exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(exception));
    }

}
