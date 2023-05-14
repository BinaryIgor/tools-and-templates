package io.codyn.app.template.project.core;

import java.util.UUID;

public class ProjectAccessValidator {

    public static void getOwnerAndValidateAccess(ProjectRepository projectRepository,
                                                 UUID projectId, UUID userId) {
        var currentProjectOwnerId = projectRepository.findOwnerById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFoundException(projectId));

        validateAccess(projectId, userId, currentProjectOwnerId);
    }

    public static void validateAccess(UUID projectId, UUID userId, UUID currentProjectOwnerId) {
        if (!userId.equals(currentProjectOwnerId)) {
            throw ProjectExceptions.projectForbiddenException(userId, projectId);
        }
    }
}
