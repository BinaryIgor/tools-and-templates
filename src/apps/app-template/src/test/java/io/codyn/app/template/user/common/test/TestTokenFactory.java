package io.codyn.app.template.user.common.test;

import io.codyn.app.template.user.common.core.ActivationTokenData;
import io.codyn.app.template.user.common.core.ActivationTokenFactory;
import io.codyn.test.TestRandom;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

public class TestTokenFactory implements ActivationTokenFactory.TokenFactory {

    private final Map<ActivationTokenData, String> nextTokens = new HashMap<>();
    private final ActivationTokenFactory activationTokenFactory;


    public TestTokenFactory(Clock clock) {
        activationTokenFactory = new ActivationTokenFactory(this, clock);
    }


    @Override
    public String newToken(ActivationTokenData data) {
        return nextTokens.get(data);
    }


    public String addNextToken(ActivationTokenData data, String token) {
        nextTokens.put(data, token);
        return token;
    }

    public String addNextToken(ActivationTokenData data) {
        return addNextToken(data, TestRandom.string());
    }

    public ActivationTokenFactory activationTokenFactory() {
        return activationTokenFactory;
    }
}
