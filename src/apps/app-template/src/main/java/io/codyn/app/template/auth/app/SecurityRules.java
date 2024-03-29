package io.codyn.app.template.auth.app;

import io.codyn.app.template._common.core.exception.AccessForbiddenException;
import io.codyn.app.template._common.core.exception.UnauthenticatedException;
import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template.auth.api.AuthenticatedUser;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class SecurityRules {

    private final Predicates predicates;

    public SecurityRules(Predicates predicates) {
        this.predicates = predicates;
    }

    public void validateAccess(String endpoint,
                               boolean privateClient,
                               Optional<AuthenticatedUser> user) {
        if (predicates.publicEndpoint.test(endpoint)) {
            return;
        }

        if (privateClient && predicates.privateClientEndpoint.test(endpoint)) {
            return;
        }

        if (user.isPresent()) {
            validateUserHasAccessToEndpoint(endpoint, user.get());
        } else {
            throw new UnauthenticatedException();
        }
    }

    private void validateUserHasAccessToEndpoint(String endpoint, AuthenticatedUser user) {
        if (predicates.adminEndpoint().test(endpoint) && !user.roles().containsAdmin()) {
            throw new AccessForbiddenException("User is not an admin");
        }
        if (!predicates.isUserOfStateAllowed().test(endpoint, user.state())) {
            throw new AccessForbiddenException(
                    "User of %s state can't access requested resource".formatted(user.state()));
        }
    }

    public record Predicates(Predicate<String> publicEndpoint,
                             Predicate<String> privateClientEndpoint,
                             BiPredicate<String, UserState> isUserOfStateAllowed,
                             Predicate<String> adminEndpoint) {
    }
}
