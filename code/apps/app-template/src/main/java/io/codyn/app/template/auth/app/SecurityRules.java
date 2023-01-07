package io.codyn.app.template.auth.app;

import io.codyn.app.template._shared.domain.model.UserState;
import io.codyn.app.template.auth.domain.AuthenticatedUser;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Component
public class SecurityRules {

    public void validateAccess(String endpoint,
                                  Optional<AuthenticatedUser> user) {
        //TODO: impl
    }

    public record Predicates(Predicate<String> publicEndpoint,
                             BiPredicate<String, UserState> isUserOfStateAllowed) {
    }
}
