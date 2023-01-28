package io.codyn.app.template.user.common.test;

import io.codyn.app.template.user.common.core.ActivationTokenRepository;
import io.codyn.app.template.user.common.core.model.ActivationToken;
import io.codyn.app.template.user.common.core.model.ActivationTokenId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TestActivationTokenRepository implements ActivationTokenRepository {

    private final Map<ActivationTokenId, ActivationToken> tokens = new HashMap<>();

    @Override
    public void save(ActivationToken token) {
        tokens.put(new ActivationTokenId(token.userId(), token.type()), token);
    }

    @Override
    public Optional<ActivationToken> ofId(ActivationTokenId id) {
        return Optional.ofNullable(tokens.get(id));
    }

    @Override
    public void delete(ActivationTokenId id) {
        tokens.remove(id);
    }
}
