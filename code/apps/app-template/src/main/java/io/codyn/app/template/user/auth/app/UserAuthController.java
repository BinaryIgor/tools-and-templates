package io.codyn.app.template.user.auth.app;

import io.codyn.app.template.auth.app.SecurityEndpoints;
import io.codyn.app.template.auth.core.AuthTokens;
import io.codyn.app.template.user.auth.app.model.ActivationToken;
import io.codyn.app.template.user.auth.app.model.CreateUserRequest;
import io.codyn.app.template.user.auth.app.model.RefreshToken;
import io.codyn.app.template.user.auth.core.model.*;
import io.codyn.app.template.user.auth.core.service.UserAuthService;
import io.codyn.app.template.user.auth.core.usecase.ActivateUserUseCase;
import io.codyn.app.template.user.auth.core.usecase.CreateUserUseCase;
import io.codyn.app.template.user.auth.core.usecase.ResetUserPasswordUseCase;
import io.codyn.app.template.user.auth.core.usecase.SetNewUserPasswordUseCase;
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

    private final CreateUserUseCase createUserUseCase;
    private final ActivateUserUseCase activateUserUseCase;
    private final UserAuthService userAuthService;
    private final ResetUserPasswordUseCase resetUserPasswordUseCase;
    private final SetNewUserPasswordUseCase setNewUserPasswordUseCase;

    public UserAuthController(CreateUserUseCase createUserUseCase,
                              ActivateUserUseCase activateUserUseCase,
                              UserAuthService userAuthService,
                              ResetUserPasswordUseCase resetUserPasswordUseCase,
                              SetNewUserPasswordUseCase setNewUserPasswordUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.activateUserUseCase = activateUserUseCase;
        this.userAuthService = userAuthService;
        this.resetUserPasswordUseCase = resetUserPasswordUseCase;
        this.setNewUserPasswordUseCase = setNewUserPasswordUseCase;
    }

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody CreateUserRequest request) {
        createUserUseCase.handle(request.toCommand());
    }

    @PostMapping("/activate-account")
    public void activate(@RequestBody ActivationToken activationToken) {
        activateUserUseCase.handle(activationToken.activationToken());
    }

    @PostMapping("/sign-in")
    public SignedInUserStep signIn(@RequestBody UserSignInRequest request) {
        return userAuthService.authenticate(request);
    }

    @PostMapping("/sign-in-second-step")
    public SignedInUser signInSecondStep(@RequestBody SignInSecondStepRequest request) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @PostMapping("/reset-password/{email}")
    public void resetPassword(@PathVariable("email") String email) {
        resetUserPasswordUseCase.handle(email);
    }

    @PostMapping("/set-new-password")
    public void setNewPassword(@RequestBody SetNewPasswordCommand command) {
        setNewUserPasswordUseCase.handle(command);
    }

    @PostMapping("/refresh-tokens")
    public AuthTokens refreshTokens(@RequestBody RefreshToken token) {
        return userAuthService.newTokens(token.refreshToken());
    }

}
