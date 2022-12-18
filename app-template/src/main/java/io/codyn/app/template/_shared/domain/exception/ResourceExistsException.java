package io.codyn.app.template._shared.domain.exception;

import java.util.List;

public class ResourceExistsException extends AppException {

    public ResourceExistsException(String message, List<String> reasons) {
        super(message, reasons);
    }

    public ResourceExistsException(String message, String... reasons) {
        super(message, reasons);
    }
}
