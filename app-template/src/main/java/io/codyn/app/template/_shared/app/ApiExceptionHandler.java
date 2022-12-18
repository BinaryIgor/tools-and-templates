package io.codyn.app.template._shared.app;

import io.codyn.app.template._shared.domain.ResourceExistsException;
import io.codyn.app.template._shared.domain.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
//TODO: map all exceptions
public class ApiExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleException(ValidationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception));
    }

    @ExceptionHandler(ResourceExistsException.class)
    public ResponseEntity<ErrorResponse> handleException(ResourceExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(exception, exception.reasons()));
    }

    public record ErrorResponse(String exception, String message, List<String> reasons) {

        ErrorResponse(Throwable exception, String... reasons) {
            this(exception, List.of(reasons));
        }

        ErrorResponse(Throwable exception, List<String> reasons) {
            this(exception.getClass().getSimpleName(), exception.getMessage(), reasons);
        }
    }
}
