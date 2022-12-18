package io.codyn.app.template.user.infra.entity;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserEntityRepository extends CrudRepository<UserEntity, UUID> {

    @Query("""
            SELECT * FROM "user".user WHERE email = :email""")
    Optional<UserEntity> findByEmail(String email);
}
