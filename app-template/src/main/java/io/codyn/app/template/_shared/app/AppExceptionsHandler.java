package io.codyn.app.template._shared.app;

import io.codyn.app.template._shared.domain.exception.*;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
//TODO: map all exceptions
public class AppExceptionsHandler {

    @ExceptionHandler(AppResourceExistsException.class)
    public ResponseEntity<AppErrorResponse> handleException(AppResourceExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new AppErrorResponse(exception));
    }

    @ExceptionHandler(AppResourceNotFoundException.class)
    public ResponseEntity<AppErrorResponse> handleException(AppResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new AppErrorResponse(exception));
    }

    @ExceptionHandler(AppResourceForbiddenException.class)
    public ResponseEntity<AppErrorResponse> handleException(AppResourceForbiddenException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new AppErrorResponse(exception));
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<AppErrorResponse> handleException(AppException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new AppErrorResponse(exception));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<AppErrorResponse> handleException(OptimisticLockingFailureException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new AppErrorResponse(new AppOptimisticLockException()));
    }
}
