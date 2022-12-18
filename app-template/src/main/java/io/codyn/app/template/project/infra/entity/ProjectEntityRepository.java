package io.codyn.app.template.project.infra.entity;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProjectEntityRepository extends CrudRepository<ProjectEntity, UUID> {

    @Query("SELECT owner_id FROM project.project WHERE id = :id")
    Optional<UUID> findOwnerById(UUID id);
}
