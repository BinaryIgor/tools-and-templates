package io.codyn.app.template.project.app;

import io.codyn.app.template._shared.app.exception.ApiExceptionResponse;
import io.codyn.app.template._shared.app.IdResponse;
import io.codyn.app.template._shared.app.JwtSecurityRequirement;
import io.codyn.app.template.project.app.model.ApiNewProject;
import io.codyn.app.template.project.app.model.ApiUpdateProject;
import io.codyn.app.template.project.core.ProjectService;
import io.codyn.app.template.project.core.model.AddUsersToProjectCommand;
import io.codyn.app.template.project.core.model.ProjectWithUsers;
import io.codyn.app.template.project.core.model.RemoveUsersFromProjectCommand;
import io.codyn.app.template.user.api.UserClient;
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

    private final ProjectService projectService;
    private final UserClient userClient;

    public ProjectController(ProjectService projectService,
                             UserClient userClient) {
        this.projectService = projectService;
        this.userClient = userClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiExceptionResponse.class)))
    })
    @Operation(description = """
            Some nice description of create project endpoint
            """)
    public IdResponse create(@RequestBody ApiNewProject request) {
        var userId = userClient.currentUserId();
        var project = request.toProject(userId);

        projectService.save(project);

        return new IdResponse(project.id());
    }

    @PutMapping("/{id}")
    public void update(@RequestBody ApiUpdateProject request,
                       @PathVariable UUID id) {
        var userId = userClient.currentUserId();
        projectService.save(request.toProject(id, userId));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        var userId = userClient.currentUserId();
        projectService.delete(id, userId);
    }

    @GetMapping("/{id}")
    public ProjectWithUsers get(@PathVariable UUID id) {
        var userId = userClient.currentUserId();
        return projectService.get(id, userId);
    }

    @GetMapping
    //TODO
    public List<ProjectWithUsers> getAllOfUser() {
        return List.of();
    }

    @PostMapping("/{id}/users")
    public void addUsers(@PathVariable UUID id,
                         @RequestBody List<UUID> toAddUserIds) {
        var userId = userClient.currentUserId();
        var command = new AddUsersToProjectCommand(id, userId, toAddUserIds);
        projectService.addUsers(command);
    }

    @DeleteMapping("/{id}/users")
    public void removeUsers(@PathVariable UUID id,
                            @RequestBody List<UUID> toAddUserIds) {
        var userId = userClient.currentUserId();
        var command = new RemoveUsersFromProjectCommand(id, userId, toAddUserIds);
        projectService.removeUsers(command);
    }
}
