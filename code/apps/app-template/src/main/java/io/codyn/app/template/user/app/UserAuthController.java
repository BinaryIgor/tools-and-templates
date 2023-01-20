package io.codyn.app.template.user.app;

import io.codyn.app.template.auth.app.SecurityEndpoints;
import io.codyn.app.template.auth.core.AuthTokens;
import io.codyn.app.template.user.app.model.ActivationToken;
import io.codyn.app.template.user.app.model.ApiNewUserRequest;
import io.codyn.app.template.user.app.model.RefreshToken;
import io.codyn.app.template.user.core.model.auth.*;
import io.codyn.app.template.user.core.service.NewUserService;
import io.codyn.app.template.user.core.service.UserActivationService;
import io.codyn.app.template.user.core.service.UserAuthService;
import io.codyn.app.template.user.core.service.UserPasswordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User auth (/user-auth)",
        description = """
                Endpoints to handle user-auth flow, including:
                sign-up, sign-in, password-resets and refreshing tokens
                """)
@RestController
@RequestMapping(SecurityEndpoints.USER_AUTH)
public class UserAuthController {

    private final NewUserService newUserService;
    private final UserActivationService userActivationService;
    private final UserAuthService userAuthService;
    private final UserPasswordService userPasswordService;

    public UserAuthController(NewUserService newUserService,
                              UserActivationService userActivationService,
                              UserAuthService userAuthService,
                              UserPasswordService userPasswordService) {
        this.newUserService = newUserService;
        this.userActivationService = userActivationService;
        this.userAuthService = userAuthService;
        this.userPasswordService = userPasswordService;
    }

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody ApiNewUserRequest user) {
        newUserService.create(user.toRequest());
    }

    @PostMapping("/activate-account")
    public void activate(@RequestBody ActivationToken activationToken) {
        userActivationService.activate(activationToken.activationToken());
    }

    //TODO: impl lacking services
    @PostMapping("/sign-in")
    public SignedInUserStep signIn(@RequestBody UserSignInRequest request) {
        return userAuthService.authenticate(request);
    }

    @PostMapping("/sign-in-second-step")
    public SignedInUser signInSecondStep(@RequestBody UserSignInSecondStepRequest request) {
        return userAuthService.authenticateSecondStep(request);
    }

    @PostMapping("/reset-password/{email}")
    public void resetPassword(@PathVariable("email") String email) {
        userPasswordService.resetPassword(email);
    }

    @PostMapping("/set-new-password")
    public void setNewPassword(@RequestBody NewPasswordRequest request) {
        userPasswordService.setNewPassword(request);
    }

    @PostMapping("/refresh-tokens")
    public AuthTokens refreshTokens(@RequestBody RefreshToken token) {
        return userAuthService.newTokens(token.refreshToken());
    }

}
