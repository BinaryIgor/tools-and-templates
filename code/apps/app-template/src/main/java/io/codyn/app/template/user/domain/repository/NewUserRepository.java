package io.codyn.app.template.user.domain.repository;

import io.codyn.app.template.user.domain.model.auth.NewUser;

import java.util.UUID;

public interface NewUserRepository {

    UUID create(NewUser user);
}
