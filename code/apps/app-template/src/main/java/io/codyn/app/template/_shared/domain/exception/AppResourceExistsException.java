package io.codyn.app.template._shared.domain.exception;

import java.util.List;

public class AppResourceExistsException extends AppException {

    public AppResourceExistsException(String message, List<String> reasons) {
        super(message, reasons);
    }

    public AppResourceExistsException(String message, String... reasons) {
        super(message, reasons);
    }
}
