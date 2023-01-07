package io.codyn.app.template.auth.app;

import io.codyn.app.template._shared.domain.model.UserState;

import java.util.List;

public class SecurityEndpoints {

    public static final String USER_AUTH = "/user-auth";
    public static final String ADMIN = "/admin";

    public static final List<String> PUBLIC_ENDPOINTS = List.of(USER_AUTH);

    public static boolean isPublic(String endpoint) {
        for (var e : PUBLIC_ENDPOINTS) {
            if (endpoint.startsWith(e)) {
                return true;
            }
        }
        return false;
    }

    //TODO: implement if needed
    public static boolean isUserOfStateAllowed(String endpoint, UserState state) {
        return true;
    }

    public static boolean isAdmin(String endpoint) {
        return endpoint.startsWith(ADMIN);
    }
}
