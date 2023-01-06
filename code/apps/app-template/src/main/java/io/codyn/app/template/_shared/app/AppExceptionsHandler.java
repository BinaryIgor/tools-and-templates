package io.codyn.app.template._shared.app;

import io.codyn.app.template._shared.domain.exception.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
//TODO: map all exceptions
public class AppExceptionsHandler {

    @ExceptionHandler(AppResourceExistsException.class)
    @ApiResponse(responseCode = "409", description = "Given resource exist or there was an optimist lock exception")
    public ResponseEntity<AppErrorResponse> handleException(AppResourceExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new AppErrorResponse(exception));
    }

    @ExceptionHandler(AppResourceNotFoundException.class)
    @ApiResponse(responseCode = "404", description = "Given resource can't be found")
    public ResponseEntity<AppErrorResponse> handleException(AppResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new AppErrorResponse(exception));
    }

    @ExceptionHandler(AppResourceForbiddenException.class)
    @ApiResponse(responseCode = "404", description = "Given is not available for a user")
    public ResponseEntity<AppErrorResponse> handleException(AppResourceForbiddenException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new AppErrorResponse(exception));
    }

    @ExceptionHandler(AppException.class)
    @ApiResponse(responseCode = "400", description = "Request is not correct")
    public ResponseEntity<AppErrorResponse> handleException(AppException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new AppErrorResponse(exception));
    }

    @ExceptionHandler(AppOptimisticLockException.class)
    @ApiResponse(responseCode = "409", description = "Given resource exists or there was an optimist lock exception")
    public ResponseEntity<AppErrorResponse> handleException(AppOptimisticLockException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new AppErrorResponse(exception));
    }
}
