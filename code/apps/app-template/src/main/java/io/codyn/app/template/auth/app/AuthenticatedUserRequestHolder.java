package io.codyn.app.template.auth.app;

import io.codyn.app.template._shared.domain.model.AuthenticatedUser;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public class AuthenticatedUserRequestHolder {

    static final String USER_ATTRIBUTE = "io.codyn.auth.user";

    public static void set(AuthenticatedUser user) {
        var requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes sa) {
            sa.setAttribute(USER_ATTRIBUTE, user, RequestAttributes.SCOPE_REQUEST);
        }
    }

    public static Optional<AuthenticatedUser> get() {
        var requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes sa) {
            return Optional.ofNullable((AuthenticatedUser) sa.getRequest().getAttribute(USER_ATTRIBUTE));
        }
        return Optional.empty();
    }
}
