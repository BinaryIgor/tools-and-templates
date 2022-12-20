package io.codyn.app.template.user.infra.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("user")
public record UserEntity(@Id UUID id,
                         String name,
                         String email,
                         String password,
                         String state) {
}
