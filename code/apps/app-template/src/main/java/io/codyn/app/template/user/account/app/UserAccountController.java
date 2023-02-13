package io.codyn.app.template.user.account.app;

import io.codyn.app.template.auth.api.AuthUserClient;
import io.codyn.app.template.user.account.core.model.UpdatePasswordRequest;
import io.codyn.app.template.user.account.core.UserAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User account (/user-account)",
        description = """
                Endpoints to handle user's account edition
                """)
@RestController
@RequestMapping("/user-account")
public class UserAccountController {

    private final UserAccountService service;
    private final AuthUserClient authUserClient;

    public UserAccountController(UserAccountService service,
                                 AuthUserClient authUserClient) {
        this.service = service;
        this.authUserClient = authUserClient;
    }

    @PatchMapping("/email/{email}")
    public void changeEmail(@PathVariable("email") String email) {
        var userId = authUserClient.currentId();
        service.changeEmail(userId, email);
    }

    @PostMapping("/email-change-confirmation/{token}")
    public void confirmEmailChange(@PathVariable("token") String token) {
        var userId = authUserClient.currentId();
        service.confirmEmailChange(userId, token);
    }

    @PatchMapping("/password")
    public void updatePassword(@RequestBody UpdatePasswordRequest request) {
        var userId = authUserClient.currentId();
        service.updatePassword(userId, request);
    }
}
