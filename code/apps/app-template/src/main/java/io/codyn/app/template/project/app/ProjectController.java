package io.codyn.app.template.project.app;

import io.codyn.app.template._common.app.JwtSecurityRequirement;
import io.codyn.app.template._common.app.exception.ExceptionResponse;
import io.codyn.app.template.auth.api.AuthUserClient;
import io.codyn.app.template.project.app.model.ApiNewProject;
import io.codyn.app.template.project.app.model.ApiUpdateProject;
import io.codyn.app.template.project.core.model.*;
import io.codyn.app.template.project.core.usecase.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
@JwtSecurityRequirement
public class ProjectController {

    private final SaveProjectUseCase saveProjectUseCase;
    private final GetProjectWithUsersUseCase getProjectWithUsersUseCase;
    private final DeleteProjectUseCase deleteProjectUseCase;
    private final AddUsersToProjectUseCase addUsersToProjectUseCase;
    private final RemoveUsersFromProjectUseCase removeUsersFromProjectUseCase;
    private final AuthUserClient authUserClient;

    public ProjectController(SaveProjectUseCase saveProjectUseCase,
                             GetProjectWithUsersUseCase getProjectWithUsersUseCase,
                             DeleteProjectUseCase deleteProjectUseCase,
                             AddUsersToProjectUseCase addUsersToProjectUseCase,
                             RemoveUsersFromProjectUseCase removeUsersFromProjectUseCase,
                             AuthUserClient authUserClient) {
        this.saveProjectUseCase = saveProjectUseCase;
        this.getProjectWithUsersUseCase = getProjectWithUsersUseCase;
        this.deleteProjectUseCase = deleteProjectUseCase;
        this.addUsersToProjectUseCase = addUsersToProjectUseCase;
        this.removeUsersFromProjectUseCase = removeUsersFromProjectUseCase;
        this.authUserClient = authUserClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @Operation(description = """
            Some nice description of create project endpoint
            """)
    public Project create(@RequestBody ApiNewProject request) {
        var userId = authUserClient.currentId();
        var project = request.toProject(userId);
        return saveProjectUseCase.handle(project);
    }

    @PutMapping("/{id}")
    public Project update(@RequestBody ApiUpdateProject request,
                          @PathVariable UUID id) {
        var userId = authUserClient.currentId();
        return saveProjectUseCase.handle(request.toProject(id, userId));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        var userId = authUserClient.currentId();
        deleteProjectUseCase.handle(new DeleteProjectCommand(id, userId));
    }

    @GetMapping("/{id}")
    public ProjectWithUsers get(@PathVariable UUID id) {
        var userId = authUserClient.currentId();
        return getProjectWithUsersUseCase.handle(new GetProjectWithUsersQuery(id, userId));
    }

    @GetMapping
    //TODO
    public List<ProjectWithUsers> getAllOfUser() {
        return List.of();
    }

    @PostMapping("/{id}/users")
    public void addUsers(@PathVariable UUID id,
                         @RequestBody List<UUID> toAddUserIds) {
        var userId = authUserClient.currentId();
        var command = new AddUsersToProjectCommand(id, userId, toAddUserIds);
        addUsersToProjectUseCase.handle(command);
    }

    @DeleteMapping("/{id}/users")
    public void removeUsers(@PathVariable UUID id,
                            @RequestBody List<UUID> toAddUserIds) {
        var userId = authUserClient.currentId();
        var command = new RemoveUsersFromProjectCommand(id, userId, toAddUserIds);
        removeUsersFromProjectUseCase.handle(command);
    }
}
