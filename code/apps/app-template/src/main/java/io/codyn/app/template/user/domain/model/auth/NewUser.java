package io.codyn.app.template.user.domain.model.auth;

public record NewUser(String name,
                      String email,
                      String password) {

    public NewUser withPassword(String password) {
        return new NewUser(name, email, password);
    }
}
