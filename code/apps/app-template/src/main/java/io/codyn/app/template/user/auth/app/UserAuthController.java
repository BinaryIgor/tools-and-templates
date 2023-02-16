package io.codyn.app.template.user.auth.app;

import io.codyn.app.template.auth.app.SecurityEndpoints;
import io.codyn.app.template.auth.core.AuthTokens;
import io.codyn.app.template.user.auth.app.model.ActivationToken;
import io.codyn.app.template.user.auth.app.model.CreateUserRequest;
import io.codyn.app.template.user.auth.app.model.RefreshToken;
import io.codyn.app.template.user.auth.core.model.*;
import io.codyn.app.template.user.auth.core.usecase.*;
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
    private final SignInFirstStepUseCase signInFirstStepUseCase;
    private final ResetUserPasswordUseCase resetUserPasswordUseCase;
    private final SetNewUserPasswordUseCase setNewUserPasswordUseCase;
    private final RefreshUserAuthTokensUseCase refreshUserAuthTokensUseCase;

    public UserAuthController(CreateUserUseCase createUserUseCase,
                              ActivateUserUseCase activateUserUseCase,
                              SignInFirstStepUseCase signInFirstStepUseCase,
                              ResetUserPasswordUseCase resetUserPasswordUseCase,
                              SetNewUserPasswordUseCase setNewUserPasswordUseCase,
                              RefreshUserAuthTokensUseCase refreshUserAuthTokensUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.activateUserUseCase = activateUserUseCase;
        this.signInFirstStepUseCase = signInFirstStepUseCase;
        this.resetUserPasswordUseCase = resetUserPasswordUseCase;
        this.setNewUserPasswordUseCase = setNewUserPasswordUseCase;
        this.refreshUserAuthTokensUseCase = refreshUserAuthTokensUseCase;
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
    public SignedInUserStep signIn(@RequestBody SignInFirstStepCommand command) {
        return signInFirstStepUseCase.handle(command);
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
        return refreshUserAuthTokensUseCase.handle(token.refreshToken());
    }

}
