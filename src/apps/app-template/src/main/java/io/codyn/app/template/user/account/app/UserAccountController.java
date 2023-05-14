package io.codyn.app.template.user.account.app;

import io.codyn.app.template.auth.api.AuthUserClient;
import io.codyn.app.template.user.account.core.model.ChangeUserEmailCommand;
import io.codyn.app.template.user.account.core.model.ConfirmUserEmailChangeCommand;
import io.codyn.app.template.user.account.core.model.UpdatePasswordRequest;
import io.codyn.app.template.user.account.core.model.UpdateUserPasswordCommand;
import io.codyn.app.template.user.account.core.usecase.ChangeUserEmailUseCase;
import io.codyn.app.template.user.account.core.usecase.ConfirmUserEmailChangeUseCase;
import io.codyn.app.template.user.account.core.usecase.UpdateUserPasswordUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.checkerframework.checker.units.qual.C;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User account (/user-account)",
        description = """
                Endpoints to handle user's account edition
                """)
@RestController
@RequestMapping("/user-account")
//TODO: lacking tests!
public class UserAccountController {

    private final ChangeUserEmailUseCase changeUserEmailUseCase;
    private final ConfirmUserEmailChangeUseCase confirmUserEmailChangeUseCase;
    private final UpdateUserPasswordUseCase updateUserPasswordUseCase;
    private final AuthUserClient authUserClient;

    public UserAccountController(ChangeUserEmailUseCase changeUserEmailUseCase,
                                 ConfirmUserEmailChangeUseCase confirmUserEmailChangeUseCase,
                                 UpdateUserPasswordUseCase updateUserPasswordUseCase,
                                 AuthUserClient authUserClient) {
        this.changeUserEmailUseCase = changeUserEmailUseCase;
        this.confirmUserEmailChangeUseCase = confirmUserEmailChangeUseCase;
        this.updateUserPasswordUseCase = updateUserPasswordUseCase;
        this.authUserClient = authUserClient;
    }

    @PatchMapping("/email/{email}")
    public void changeEmail(@PathVariable("email") String email) {
        var userId = authUserClient.currentId();
        changeUserEmailUseCase.handle(new ChangeUserEmailCommand(userId, email));
    }

    @PostMapping("/email-change-confirmation/{token}")
    public void confirmEmailChange(@PathVariable("token") String token) {
        var userId = authUserClient.currentId();
        confirmUserEmailChangeUseCase.handle(new ConfirmUserEmailChangeCommand(userId, token));
    }

    @PatchMapping("/password")
    public void updatePassword(@RequestBody UpdatePasswordRequest request) {
        var userId = authUserClient.currentId();
        updateUserPasswordUseCase.handle(new UpdateUserPasswordCommand(userId,
                request.oldPassword(), request.newPassword()));
    }
}
