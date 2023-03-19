package io.codyn.app.template.user.common.infra;

import io.codyn.app.template.user.common.core.model.ActivationToken;
import io.codyn.app.template.user.common.core.model.ActivationTokenId;
import io.codyn.app.template.user.common.core.model.ActivationTokenStatus;
import io.codyn.app.template._common.core.model.ActivationTokenType;
import io.codyn.app.template.user.common.core.repository.ActivationTokenRepository;
import io.codyn.app.template.user.common.core.repository.ActivationTokenStatusUpdateRepository;
import io.codyn.sqldb.core.DSLContextProvider;
import org.jooq.Condition;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static io.codyn.sqldb.schema.user.tables.ActivationToken.ACTIVATION_TOKEN;

//TODO: lacking tests
@Repository
public class SqlActivationTokenRepository implements ActivationTokenRepository, ActivationTokenStatusUpdateRepository {

    private final DSLContextProvider contextProvider;

    public SqlActivationTokenRepository(DSLContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override
    public void save(ActivationToken token) {
        contextProvider.context()
                .insertInto(ACTIVATION_TOKEN)
                .set(ACTIVATION_TOKEN.USER_ID, token.userId())
                .set(ACTIVATION_TOKEN.TYPE, token.type().name())
                .set(ACTIVATION_TOKEN.STATUS, token.status().name())
                .set(ACTIVATION_TOKEN.TOKEN, token.token())
                .set(ACTIVATION_TOKEN.EXPIRES_AT, token.expiresAt())
                .onConflict()
                .doUpdate()
                .set(ACTIVATION_TOKEN.STATUS, token.status().name())
                .set(ACTIVATION_TOKEN.TOKEN, token.token())
                .set(ACTIVATION_TOKEN.EXPIRES_AT, token.expiresAt())
                .execute();
    }

    @Override
    public void update(ActivationTokenId id, ActivationTokenStatus status) {
        contextProvider.context()
                .update(ACTIVATION_TOKEN)
                .set(ACTIVATION_TOKEN.STATUS, status.name())
                .where(idEqualsCondition(id))
                .execute();
    }

    @Override
    public Optional<ActivationToken> ofId(ActivationTokenId id) {
        return contextProvider.context()
                .selectFrom(ACTIVATION_TOKEN)
                .where(idEqualsCondition(id))
                .fetchOptional(r -> new ActivationToken(r.getUserId(),
                        ActivationTokenType.valueOf(r.getType()),
                        ActivationTokenStatus.valueOf(r.getStatus()),
                        r.getToken(),
                        r.getExpiresAt()));
    }

    private Condition idEqualsCondition(ActivationTokenId id) {
        return ACTIVATION_TOKEN.USER_ID.eq(id.userId())
                .and(ACTIVATION_TOKEN.TYPE.eq(id.tokenType().name()));
    }

    @Override
    public void delete(ActivationTokenId id) {
        contextProvider.context()
                .deleteFrom(ACTIVATION_TOKEN)
                .where(idEqualsCondition(id))
                .execute();
    }
}
