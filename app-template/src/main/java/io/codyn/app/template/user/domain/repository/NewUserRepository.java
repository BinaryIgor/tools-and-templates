package io.codyn.app.template.user.domain.repository;

import io.codyn.app.template.user.domain.model.NewUser;

import java.util.UUID;

public interface NewUserRepository {

    UUID create(NewUser user);
}
