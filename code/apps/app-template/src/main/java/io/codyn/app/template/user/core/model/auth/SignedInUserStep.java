package io.codyn.app.template.user.core.model.auth;

public record SignedInUserStep(boolean secondFactor, SignedInUser user) {

    //TODO: impl
    public static SignedInUserStep firstStep() {
        return new SignedInUserStep(true, null);
    }

    public static SignedInUserStep onlyStep(SignedInUser user) {
        return new SignedInUserStep(false, user);
    }
}
