package io.codyn.app.template.project.infra;

import io.codyn.app.template.project.domain.model.Project;
import io.codyn.commons.sqldb.schema.project.tables.records.ProjectRecord;

public class ProjectRecordsMapper {

    public static ProjectRecord setFromProject(ProjectRecord record, Project project) {
        return record.setId(project.id())
                .setName(project.name())
                .setOwnerId(project.ownerId())
                .setVersion(project.version());
    }

    public static Project fromProjectRecord(ProjectRecord record) {
        return new Project(record.getId(), record.getOwnerId(), record.getName(), record.getVersion());
    }
}
