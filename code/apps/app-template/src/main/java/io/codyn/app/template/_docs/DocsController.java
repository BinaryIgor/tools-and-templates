package io.codyn.app.template._docs;

import io.codyn.app.template._shared.core.exception.*;
import io.codyn.app.template.user.core.exception.InvalidActivationTokenException;
import io.codyn.app.template.user.core.exception.InvalidPasswordException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@Profile("local")
@RestController
@RequestMapping("/docs")
public class DocsController {

    @GetMapping("/errors")
    public ErrorDocs errors() {
        return new ErrorDocs.Builder()
                .add(AccessForbiddenException.class)
                .add(ConflictException.class)
                .add(EmailNotReachableException.class)
                .add(EmailTakenException.class)
                .add(InvalidAuthTokenException.class)
                .add(NotFoundException.class)
                .add(OptimisticLockException.class)
                .add(UnauthenticatedException.class)
                .add(ValidationException.class)
                .add(InvalidActivationTokenException.class)
                .add(InvalidPasswordException.class)
                //From Nginx errors
                .add("BodyTooLargeException")
                .add("ApiUnavailableException")
                .add("TooManyRequestsException")
                .build();
    }
}
