package io.codyn.app.template.user.domain.repository;

import io.codyn.app.template.user.domain.model.auth.ToSignInUser;

import java.util.Optional;

public interface UserAuthRepository {
    Optional<ToSignInUser> toSignInUserOfEmail(String email);
}
