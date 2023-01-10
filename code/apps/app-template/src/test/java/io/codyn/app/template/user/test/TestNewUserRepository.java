package io.codyn.app.template.user.test;

import io.codyn.app.template.user.domain.model.auth.NewUser;
import io.codyn.app.template.user.domain.repository.NewUserRepository;

import java.util.UUID;

public class TestNewUserRepository implements NewUserRepository {

    public UUID nextId;
    public NewUser createdUser;

    @Override
    public UUID create(NewUser user) {
        createdUser = user;
        return nextId;
    }
}
