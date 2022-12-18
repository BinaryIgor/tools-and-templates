package io.codyn.app.template.project.infra.entity;

import io.codyn.app.template.project.domain.Project;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(schema = "project", value = "project")
public record ProjectEntity(@Id UUID id,
                            UUID ownerId,
                            String name,
                            @Version long version) {

    public static ProjectEntity fromProject(Project project) {
        return new ProjectEntity(project.id(), project.ownerId(), project.name(), project.version());
    }

    public Project toProject() {
        return new Project(id, ownerId, name, version);
    }
}
