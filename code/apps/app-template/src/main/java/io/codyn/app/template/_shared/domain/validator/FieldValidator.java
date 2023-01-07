package io.codyn.app.template._shared.domain.validator;

import io.codyn.app.template._shared.domain.exception.ValidationException;

import java.util.regex.Pattern;

public class FieldValidator {

    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 30;
    public static final int MAX_EMAIL_LENGTH = 150;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 50;
    private static final Pattern HTML_CHARACTERS_REGEX = Pattern.compile("(?s)(.*)<(.+)>(?s)(.*)");

    public static boolean isEmailValid(String email) {
        var invalid = email == null ||
                hasHtmlCharacters(email) ||
                email.length() > MAX_EMAIL_LENGTH;
        if (invalid) {
            return false;
        }

        var atIdx = email.indexOf("@");
        if (atIdx < 1) {
            return false;
        }

        var name = email.substring(0, atIdx);
        if (!hasAtLeastOneLetterOrDigit(name)) {
            return false;
        }

        var domain = email.substring(atIdx + 1);
        var dotIndex = domain.indexOf(".");

        return dotIndex > 0 && dotIndex < (domain.length() - 1);
    }

    private static boolean hasAtLeastOneLetterOrDigit(String string) {
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (Character.isLetter(c) || Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    public static void validateEmail(String email) {
        if (!isEmailValid(email)) {
            throw ValidationException.ofField("email", email);
        }
    }

    public static boolean isNameValid(String name) {
        var invalid = name == null ||
                name.strip().length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH ||
                hasHtmlCharacters(name);
        if (invalid) {
            return false;
        }

        return hasAtLeastOneLetter(name);
    }

    public static void validateName(String name) {
        if (!isNameValid(name)) {
            throw ValidationException.ofField("name", name);
        }
    }

    private static boolean hasAtLeastOneLetter(String string) {
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPasswordValid(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            return false;
        }
        var alpha = false;
        var digit = false;
        var upper = false;
        var lower = false;

        for (var c : password.toCharArray()) {
            if (Character.isAlphabetic(c) && !alpha) {
                alpha = true;
            }
            if (Character.isUpperCase(c) && !upper) {
                upper = true;
            }
            if (Character.isLowerCase(c) && !lower) {
                lower = true;
            }
            if (Character.isDigit(c) && !digit) {
                digit = true;
            }
        }

        return alpha && digit && upper && lower;
    }

    public static void validatePassword(String password) {
        if (!isPasswordValid(password)) {
            throw ValidationException.ofField("password", password);
        }
    }

    public static boolean hasAnyContent(String string) {
        return !(string == null || string.isBlank());
    }

    public static boolean isLongerThan(String string, int length) {
        return string != null && string.length() > length;
    }

    public static boolean hasHtmlCharacters(String string) {
        return string != null && HTML_CHARACTERS_REGEX.matcher(string).matches();
    }

}
