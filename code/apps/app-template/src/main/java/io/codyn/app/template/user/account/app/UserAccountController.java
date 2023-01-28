package io.codyn.app.template.user.account.app;

import io.codyn.app.template.user.account.core.UpdatePasswordRequest;
import io.codyn.app.template.user.account.core.UserAccountService;
import io.codyn.app.template.user.api.UserClient;
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
    private final UserClient userClient;

    public UserAccountController(UserAccountService service,
                                 UserClient userClient) {
        this.service = service;
        this.userClient = userClient;
    }

    @PatchMapping("/email/{email}")
    public void changeEmail(@PathVariable("email") String email) {
        var userId = userClient.currentUserId();
        service.changeEmail(userId, email);
    }

    @PostMapping("/email-change-confirmation/{token}")
    public void confirmEmailChange(@PathVariable("token") String token) {
        var userId = userClient.currentUserId();
        service.confirmEmailChange(userId, token);
    }

    @PatchMapping("/password")
    public void updatePassword(@RequestBody UpdatePasswordRequest request) {
        var userId = userClient.currentUserId();
        service.updatePassword(userId, request);
    }
}
