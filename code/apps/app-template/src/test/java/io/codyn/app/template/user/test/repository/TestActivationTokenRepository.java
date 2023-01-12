package io.codyn.app.template.user.test.repository;

import io.codyn.app.template.user.domain.model.activation.ActivationToken;
import io.codyn.app.template.user.domain.model.activation.ActivationTokenId;
import io.codyn.app.template.user.domain.repository.ActivationTokenRepository;

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
