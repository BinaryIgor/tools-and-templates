package io.codyn.app.template.project.infra.entity;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectEntityRepository extends CrudRepository<ProjectEntity, UUID> {

    @Query("SELECT owner_id FROM project.project WHERE id = :id")
    Optional<UUID> findOwnerById(@Param("id") UUID id);

    @Query("SELECT user_id FROM project.project_user WHERE project_id = :id")
    List<UUID> findProjectUsersById(@Param("id") UUID id);
}
