package io.codyn.app.template.user.common.infra;

import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template.user.common.core.model.User;
import io.codyn.sqldb.schema.user.tables.records.UserRecord;

public class UserRecordsMapper {

    public static UserRecord setFromUser(UserRecord record, User user) {
        return record.setId(user.id())
                .setName(user.name())
                .setEmail(user.email())
                .setPassword(user.password())
                .setState(user.state().name())
                .setSecondFactorAuth(user.secondFactorAuth());
    }

    public static User fromUserRecord(UserRecord record) {
        return new User(record.getId(), record.getName(), record.getEmail(), record.getPassword(),
                UserState.valueOf(record.getState()), record.getSecondFactorAuth());
    }
}
