package io.codyn.app.sockets.server.template;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private static final int MEGA_BYTE = 1024 * 1024;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/")
                .setAllowedOrigins("*")
//                .setAllowedOrigins("http://localhost", "http://test")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                                   org.springframework.web.socket.WebSocketHandler wsHandler,
                                                   Map<String, Object> attributes) throws Exception {

                        System.out.println("Intercepting request with %d headers...".formatted(request.getHeaders().size()));
                        request.getHeaders().forEach((k, v) -> {
                            System.out.println("%s - %s".formatted(k, v));
                        });
                        System.out.println(".....");

                        return true;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                               org.springframework.web.socket.WebSocketHandler wsHandler,
                                               Exception exception) {
                        System.out.println("After handshake...");
                        if (exception == null) {
                            System.out.println("No exception!");
                        } else {
                            exception.printStackTrace();
                        }
                        System.out.println(response);
                    }
                });
                //.setAllowedOrigins(); etc
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(MEGA_BYTE);
        container.setMaxBinaryMessageBufferSize(MEGA_BYTE);
        return container;
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new WebSocketHandler();
    }
}
