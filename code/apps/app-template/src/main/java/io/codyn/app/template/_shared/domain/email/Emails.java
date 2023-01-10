package io.codyn.app.template._shared.domain.email;


import java.util.List;
import java.util.Map;

import static io.codyn.app.template._shared.domain.email.Emails.Types.*;
import static io.codyn.app.template._shared.domain.email.Emails.Variables.*;

public class Emails {

    public static final Map<String, List<String>> TYPES_VARIABLES =
            Map.of(USER_ACTIVATION, List.of(USER, ACTIVATION_URL, SIGN_UP_URL),
                    PASSWORD_RESET, List.of(USER, NEW_PASSWORD_URL, PASSWORD_RESET_URL),
                    EMAIL_CHANGE, List.of(USER, OLD_EMAIL, EMAIL_CHANGE_CONFIRMATION_URL),
                    SECOND_FACTOR_AUTHENTICATION, List.of(USER, CODE));

    public static class Types {
        public static final String USER_ACTIVATION = "user_activation";
        public static final String PASSWORD_RESET = "password_reset";
        public static final String EMAIL_CHANGE = "email_change";
        public static final String SECOND_FACTOR_AUTHENTICATION = "second_factor_authentication";
    }

    public static class Variables {
        public static final String USER = "user";
        public static final String ACTIVATION_URL = "activationUrl";
        public static final String SIGN_UP_URL = "signUpUrl";
        public static final String OLD_EMAIL = "oldEmail";
        public static final String EMAIL_CHANGE_CONFIRMATION_URL = "emailChangeConfirmationUrl";
        public static final String NEW_PASSWORD_URL = "newPasswordUrl";
        public static final String PASSWORD_RESET_URL = "passwordResetUrl";
        public static final String CODE = "code";
    }

}
