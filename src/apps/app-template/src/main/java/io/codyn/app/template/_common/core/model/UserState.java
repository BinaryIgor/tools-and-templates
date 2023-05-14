package io.codyn.app.template._common.core.model;

public enum UserState {
    CREATED, ACTIVATED, ONBOARDED, BANNED;

    public boolean isAtLeast(UserState state) {
        return ordinal() >= state.ordinal();
    }
}
