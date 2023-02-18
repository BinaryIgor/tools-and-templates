package io.codyn.app.template.project.core.usecase;

import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template.project.core.ProjectAccessValidator;
import io.codyn.app.template.project.core.ProjectRepository;
import io.codyn.app.template.project.core.model.Project;
import org.springframework.stereotype.Component;

@Component
public class SaveProjectUseCase {

    private final ProjectRepository projectRepository;

    public SaveProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project handle(Project project) {
        validateProject(project);

        if (project.version() != 0) {
            ProjectAccessValidator.getOwnerAndValidateAccess(projectRepository,
                    project.id(), project.ownerId());
        }

        return projectRepository.save(project);
    }

    private void validateProject(Project project) {
        FieldValidator.validateName(project.name());
    }
}
