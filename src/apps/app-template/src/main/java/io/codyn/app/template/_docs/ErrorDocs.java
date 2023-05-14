package io.codyn.app.template._docs;

import io.codyn.app.template._common.core.exception.AppException;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public record ErrorDocs(Collection<String> description, Collection<String> errors) {

    public static class Builder {
        private final Set<String> errors = new LinkedHashSet<>();

        public Builder add(List<String> errors) {
            this.errors.addAll(errors);
            return this;
        }

        public Builder add(String error) {
            errors.add(error);
            return this;
        }

        public Builder add(Class<? extends Throwable> exception) {
            return add(AppException.defaultErrors(exception));
        }

        public ErrorDocs build() {
            return new ErrorDocs(
                    List.of("List of all possible errors that client should handle.",
                            "In most cases, single error is returned but there could also be a list of them.",
                            "In message there can be details helping with troubleshooting issues."),
                    errors);
        }
    }
}
