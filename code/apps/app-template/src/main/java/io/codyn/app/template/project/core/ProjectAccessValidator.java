package io.codyn.app.template.project.core;

import io.codyn.app.template._common.core.exception.AccessForbiddenException;
import io.codyn.app.template._common.core.exception.NotFoundException;

import java.util.UUID;

public class ProjectAccessValidator {

    public static void getOwnerAndValidateAccess(ProjectRepository projectRepository,
                                                 UUID projectId, UUID userId) {
        var currentProjectOwnerId = projectRepository.findOwnerById(projectId)
                .orElseThrow(() -> new NotFoundException(
                        "There is no project of %s id".formatted(projectId)));

        validateAccess(projectId, userId, currentProjectOwnerId);
    }

    public static void validateAccess(UUID projectId, UUID userId, UUID currentProjectOwnerId) {
        if (!userId.equals(currentProjectOwnerId)) {
            throw new AccessForbiddenException("%s user doesn't have access to %s project"
                    .formatted(userId, projectId));
        }
    }
}
