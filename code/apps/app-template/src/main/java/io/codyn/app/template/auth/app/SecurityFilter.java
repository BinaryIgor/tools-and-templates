package io.codyn.app.template.auth.app;

import io.codyn.app.template._shared.app.exception.ApiExceptionResponse;
import io.codyn.app.template.auth.domain.AuthTokenComponent;
import io.codyn.app.template.auth.domain.AuthenticatedUser;
import io.codyn.commons.json.JsonMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class SecurityFilter implements Filter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer";
    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

    private final SecurityRules securityRules;
    private final AuthTokenComponent authTokenComponent;

    public SecurityFilter(SecurityRules securityRules,
                          AuthTokenComponent authTokenComponent) {
        this.securityRules = securityRules;
        this.authTokenComponent = authTokenComponent;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        log.info("Doing some logging in Security filter, on a thread: " + Thread.currentThread());

        var request = (HttpServletRequest) servletRequest;

        try {
            var user = userFromRequest(request);

            securityRules.validateAccess(request.getRequestURI(), user);

            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            sendNotAuthenticatedResponse((HttpServletResponse) servletResponse, e);
        }
    }

    private Optional<AuthenticatedUser> userFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER))
                .flatMap(a -> {
                    if (a.startsWith(TOKEN_PREFIX)) {
                        var token = a.substring(TOKEN_PREFIX.length()).strip();
                        return Optional.of(authTokenComponent.authenticate(token));
                    }
                    return Optional.empty();
                });
    }

    private void sendNotAuthenticatedResponse(HttpServletResponse response, Throwable exception) {
        response.setStatus(401);
        try {
            response.setHeader("content-type", "application/json");
            response.getWriter()
                    .write(JsonMapper.json(new ApiExceptionResponse(exception)));
        } catch (Exception e) {
            log.error("Problem while writing response body to HttpServletResponse", e);
        }
    }
}
