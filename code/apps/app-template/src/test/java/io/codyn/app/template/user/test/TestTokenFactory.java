package io.codyn.app.template.user.test;

import io.codyn.app.template.user.domain.component.ActivationTokenData;
import io.codyn.app.template.user.domain.component.ActivationTokenFactory;

import java.util.HashMap;
import java.util.Map;

public class TestTokenFactory implements ActivationTokenFactory.TokenFactory {

    private final Map<ActivationTokenData, String> nextTokens = new HashMap<>();

    @Override
    public String newToken(ActivationTokenData data) {
        return nextTokens.get(data);
    }


    public String addNextToken(ActivationTokenData data, String token) {
        nextTokens.put(data, token);
        return token;
    }
}
