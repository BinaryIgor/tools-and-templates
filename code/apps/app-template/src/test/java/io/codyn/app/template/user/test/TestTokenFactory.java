package io.codyn.app.template.user.test;

import io.codyn.app.template.user.domain.component.ActivationTokenFactory;

import java.util.HashMap;
import java.util.Map;

public class TestTokenFactory implements ActivationTokenFactory.TokenFactory {

    private final Map<String, String> nextTokens = new HashMap<>();

    @Override
    public String newToken(String... data) {
        return nextTokens.get(tokenKey(data));
    }

    private String tokenKey(String... data) {
        return String.join(":", data);
    }

    public String addNextToken(String token, String... data) {
        nextTokens.put(tokenKey(data), token);
        return token;
    }
}
