package io.codyn.app.template.auth.app;

import io.codyn.app.template._common.app.exception.ExceptionResponse;
import io.codyn.app.template._common.core.exception.AccessForbiddenException;
import io.codyn.app.template._common.core.exception.InvalidAuthTokenException;
import io.codyn.app.template._common.core.exception.UnauthenticatedException;
import io.codyn.app.template.auth.api.AuthenticatedUser;
import io.codyn.app.template.auth.core.AuthTokenAuthenticator;
import io.codyn.json.JsonMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityFilter implements Filter {

    static final String REAL_IP_HEADER = "X-Real-IP";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer";
    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

    private final SecurityRules securityRules;
    private final AuthTokenAuthenticator authTokenAuthenticator;
    private final String allowedPrivateIpPrefix;

    public SecurityFilter(SecurityRules securityRules,
                          AuthTokenAuthenticator authTokenAuthenticator,
                          @Value("${app.allowed-private-ip-prefix}") String allowedPrivateIpPrefix) {
        this.securityRules = securityRules;
        this.authTokenAuthenticator = authTokenAuthenticator;
        this.allowedPrivateIpPrefix = allowedPrivateIpPrefix;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) {
        var request = (HttpServletRequest) servletRequest;
        var response = (HttpServletResponse) servletResponse;

        try {
            var user = userFromRequest(request);
            user.ifPresent(AuthenticatedUserRequestHolder::set);

            securityRules.validateAccess(request.getRequestURI(),
                    isAllowedPrivateClientRequest(request), user);

            filterChain.doFilter(servletRequest, servletResponse);
        } catch (UnauthenticatedException | InvalidAuthTokenException e) {
            sendExceptionResponse(request, response, 401, e);
        } catch (AccessForbiddenException e) {
            sendExceptionResponse(request, response, 403, e);
        } catch (Exception e) {
            e.printStackTrace();
            sendExceptionResponse(request, response, 400, e);
        }
    }

    //TODO test
    private boolean isAllowedPrivateClientRequest(HttpServletRequest request) {
        var clientIp = Optional.ofNullable(request.getHeader(REAL_IP_HEADER))
                .orElseGet(request::getRemoteAddr);

        System.out.println("Client ip..." + clientIp);

        return clientIp.startsWith(allowedPrivateIpPrefix) || isLocalhost(clientIp);
    }

    private boolean isLocalhost(String clientIp) {
        return clientIp.startsWith("localhost") || clientIp.startsWith("0.0.0.0") ||
                clientIp.startsWith("127.0.0.1") || clientIp.startsWith("::1");
    }

    private Optional<AuthenticatedUser> userFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER))
                .flatMap(a -> {
                    if (a.startsWith(TOKEN_PREFIX)) {
                        var token = a.substring(TOKEN_PREFIX.length()).strip();
                        return Optional.of(authTokenAuthenticator.authenticate(token));
                    }
                    return Optional.empty();
                });
    }

    private void sendExceptionResponse(HttpServletRequest request,
                                       HttpServletResponse response,
                                       int status,
                                       Throwable exception) {
        log.warn("Sending {} status to {}: {} request", status, request.getMethod(), request.getRequestURI());
        response.setStatus(status);
        try {
            response.setHeader("content-type", "application/json");
            response.getWriter()
                    .write(JsonMapper.json(new ExceptionResponse(exception)));
        } catch (Exception e) {
            log.error("Problem while writing response body to HttpServletResponse", e);
        }
    }
}
