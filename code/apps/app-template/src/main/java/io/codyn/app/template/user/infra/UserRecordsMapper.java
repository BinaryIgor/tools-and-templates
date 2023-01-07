package io.codyn.app.template.user.infra;

import io.codyn.app.template._shared.domain.model.UserState;
import io.codyn.app.template.user.domain.model.NewUser;
import io.codyn.app.template.user.domain.model.User;
import io.codyn.commons.sqldb.schema.user.tables.records.UserRecord;

import java.util.UUID;

public class UserRecordsMapper {

    public static UserRecord setFromNewUser(UserRecord record, NewUser newUser) {
        return record.setId(UUID.randomUUID())
                .setName(newUser.name())
                .setEmail(newUser.email())
                .setPassword(newUser.password());
    }

    public static User fromUserRecord(UserRecord record) {
        return new User(record.getId(), record.getName(), record.getEmail(), record.getPassword(),
                UserState.valueOf(record.getName()));
    }
}
