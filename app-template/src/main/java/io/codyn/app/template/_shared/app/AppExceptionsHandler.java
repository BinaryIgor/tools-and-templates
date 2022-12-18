package io.codyn.app.template._shared.app;

import io.codyn.app.template._shared.domain.exception.AppException;
import io.codyn.app.template._shared.domain.exception.AppResourceExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
//TODO: map all exceptions
public class AppExceptionsHandler {

    @ExceptionHandler(AppResourceExistsException.class)
    public ResponseEntity<ErrorResponse> handleException(AppResourceExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(exception));
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleException(AppException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception));
    }

    public record ErrorResponse(String exception, String message, List<String> reasons) {

        ErrorResponse(Throwable exception, String... reasons) {
            this(exception, List.of(reasons));
        }

        ErrorResponse(Throwable exception, List<String> reasons) {
            this(exception.getClass().getSimpleName(), exception.getMessage(), reasons);
        }

        ErrorResponse(AppException exception) {
            this(exception, exception.reasons());
        }
    }
}
