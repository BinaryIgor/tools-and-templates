package io.codyn.app.template.user.infra;

import io.codyn.app.template.user.domain.model.NewUser;
import io.codyn.app.template.user.domain.model.UserState;
import io.codyn.app.template.user.domain.repository.NewUserRepository;
import io.codyn.app.template.user.infra.entity.UserEntity;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class SqlNewUserRepository implements NewUserRepository {

    private final JdbcAggregateTemplate aggregateTemplate;

    public SqlNewUserRepository(JdbcAggregateTemplate aggregateTemplate) {
        this.aggregateTemplate = aggregateTemplate;
    }

    @Override
    public UUID create(NewUser user) {
        var entity = new UserEntity(UUID.randomUUID(),
                user.name(), user.email(), user.password(),
                UserState.CREATED.name());
        return aggregateTemplate.insert(entity).id();
    }
}
