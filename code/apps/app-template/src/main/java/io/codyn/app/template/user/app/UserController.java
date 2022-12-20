package io.codyn.app.template.user.app;

import io.codyn.app.template.user.domain.NewUserService;
import io.codyn.app.template.user.domain.model.NewUser;
import io.codyn.app.template.user.domain.model.SignInUser;
import io.codyn.app.template.user.domain.model.SignedInUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final NewUserService newUserService;

    public UserController(NewUserService newUserService) {
        this.newUserService = newUserService;
    }

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody NewUser user) {
        newUserService.create(user);
    }

    @PutMapping("/activate/{activationToken}")
    public void activate(@PathVariable("activationToken") String activationToken) {

    }

    //TODO: impl
    @PostMapping("/sign-in")
    public SignedInUser signIn(@RequestBody SignInUser user) {
        return null;
    }


}
