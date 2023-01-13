package io.codyn.app.template.user.app;

import io.codyn.app.template.auth.app.SecurityEndpoints;
import io.codyn.app.template.auth.domain.AuthTokens;
import io.codyn.app.template.user.app.model.ActivationToken;
import io.codyn.app.template.user.app.model.RefreshToken;
import io.codyn.app.template.user.domain.service.NewUserService;
import io.codyn.app.template.user.domain.service.UserActivationService;
import io.codyn.app.template.user.domain.model.auth.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(SecurityEndpoints.USER_AUTH)
public class UserAuthController {

    private final NewUserService newUserService;
    private final UserActivationService userActivationService;

    public UserAuthController(NewUserService newUserService,
                              UserActivationService userActivationService) {
        this.newUserService = newUserService;
        this.userActivationService = userActivationService;
    }

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody NewUser user) {
        newUserService.create(user);
    }

    @PostMapping("/activate-account")
    public void activate(@RequestBody ActivationToken activationToken) {
        userActivationService.activate(activationToken.activationToken());
    }

    //TODO: impl lacking endpoints
    @PostMapping("/sign-in")
    public SignedInUserStep signIn(@RequestBody UserSignInRequest request) {
        return null;
    }

    @PostMapping("/sign-in-second-step")
    public SignedInUser signInSecondStep(@RequestBody UserSignInSecondStepRequest request) {
        return null;
    }

    @PostMapping("/reset-password/{email}")
    public void resetPassword(@PathVariable("email") String email) {

    }

    @PostMapping("/set-new-password")
    public void setNewPassword(@RequestBody NewPasswordRequest request) {

    }

    @PostMapping("/refresh-tokens")
    public AuthTokens refreshTokens(@RequestBody RefreshToken token) {
        return null;
    }

}
