package io.codyn.app.template._shared.app;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class BadRequestsInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(BadRequestsInterceptor.class);

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        if (response.getStatus() < 200 || response.getStatus() >= 300) {
            log.info("Request: {}:{} got unsuccessful response: {}",  request.getMethod(), request.getRequestURI(),
                    response.getStatus());
        }
    }
}
