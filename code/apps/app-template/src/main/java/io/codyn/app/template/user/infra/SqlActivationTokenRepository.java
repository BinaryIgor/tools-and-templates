package io.codyn.app.template.user.infra;

import io.codyn.app.template.user.domain.model.activation.ActivationToken;
import io.codyn.app.template.user.domain.model.activation.ActivationTokenId;
import io.codyn.app.template.user.domain.repository.ActivationTokenRepository;
import io.codyn.sqldb.core.DSLContextProvider;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//TODO: impl!
@Repository
public class SqlActivationTokenRepository implements ActivationTokenRepository {

    private final DSLContextProvider contextProvider;

    public SqlActivationTokenRepository(DSLContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override
    public void save(ActivationToken token) {

    }

    @Override
    public Optional<ActivationToken> ofId(ActivationTokenId id) {
        return Optional.empty();
    }

    @Override
    public void delete(ActivationTokenId id) {

    }
}
