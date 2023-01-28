package io.codyn.app.template.user.account.core;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserAccountService {

    public void changeEmail(UUID id, String newEmail) {
        //TODO: impl
    }

    public void confirmEmailChange(UUID id, String token) {
        //TODO: impl
    }

    public void updatePassword(UUID id, UpdatePasswordRequest request) {
        //TODO: impl
    }
}
