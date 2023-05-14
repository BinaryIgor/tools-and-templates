package io.codyn.app.template.auth.app;

import io.codyn.app.template._common.core.model.UserState;

import java.util.List;

public class SecurityEndpoints {

    public static final String USER_AUTH = "/user-auth";
    public static final String ADMIN = "/admin";
    public static final String WEBHOOKS = "/webhooks";

    //This is fine since swagger is on only locally. See _docs module
    public static final List<String> PUBLIC_ENDPOINTS = List.of(USER_AUTH, WEBHOOKS, "/swagger", "/docs", "/ws");

    public static boolean isPublic(String endpoint) {
        for (var e : PUBLIC_ENDPOINTS) {
            if (endpoint.startsWith(e)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMetricEndpoint(String endpoint) {
        return endpoint.startsWith("/actuator");
    }

    //TODO: implement if needed
    public static boolean isUserOfStateAllowed(String endpoint, UserState state) {
        return true;
    }

    public static boolean isAdmin(String endpoint) {
        return endpoint.startsWith(ADMIN);
    }
}
