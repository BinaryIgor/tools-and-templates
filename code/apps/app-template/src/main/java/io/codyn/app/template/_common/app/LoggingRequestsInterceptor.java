package io.codyn.app.template._common.app;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoggingRequestsInterceptor implements HandlerInterceptor {

    private static final String NO_USER_ID = "ANONYMOUS";
    private static final Logger log = LoggerFactory.getLogger(LoggingRequestsInterceptor.class);

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        var status = HttpStatus.valueOf(response.getStatus());
        if (status.is2xxSuccessful()) {
            log.info("Request of user {}: {}: {} got successful response: {}", currentUserId(),
                    request.getMethod(), request.getRequestURI(), response.getStatus());
        } else if (status.is3xxRedirection()) {
            log.info("Request of user {}: {}: {} got redirect response: {}", currentUserId(),
                    request.getMethod(), request.getRequestURI(), response.getStatus());
        } else {
            log.warn("Request of user {}: {}: {} got unsuccessful response: {}", currentUserId(),
                    request.getMethod(), request.getRequestURI(), response.getStatus());
        }
    }

    private String currentUserId() {
        return HttpRequestAttributes.get(HttpRequestAttributes.USER_ID_ATTRIBUTE, String.class).orElse(NO_USER_ID);
    }
}
