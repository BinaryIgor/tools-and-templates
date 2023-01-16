package io.codyn.app.template._docs;

import java.util.ArrayList;
import java.util.List;

public record ErrorDocs(List<String> description, List<ErrorDoc> errors) {

    public record ErrorDoc(String error,
                           List<String> reasons,
                           String message) {

        public ErrorDoc(String error, List<String> reasons) {
            this(error, reasons, "Any optional string");
        }

        public ErrorDoc(Class<? extends Throwable> exception,
                        List<String> reasons) {
            this(exception.getSimpleName(), reasons);
        }

        public ErrorDoc(Class<? extends Throwable> exception) {
            this(exception, List.of());
        }
    }

    public static class Builder {
        private final List<ErrorDoc> errorDocs = new ArrayList<>();

        public Builder add(Class<? extends Throwable> exception, String... reasons) {
            errorDocs.add(new ErrorDoc(exception, List.of(reasons)));
            return this;
        }

        public Builder add(String exception) {
            errorDocs.add(new ErrorDoc(exception, List.of()));
            return this;
        }

        public ErrorDocs build() {
            return new ErrorDocs(
                    List.of("List of all possible errors that client should handle.",
                            "Reasons are specific to concrete error type and are mostly optional.",
                            "In message there can be details helping with troubleshooting issues."),
                    errorDocs);
        }
    }
}
