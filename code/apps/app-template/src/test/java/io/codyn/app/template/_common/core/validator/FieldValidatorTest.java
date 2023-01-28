package io.codyn.app.template._common.core.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class FieldValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {"Igor", "Olek", "_o", "Oo", "o123", "___o1234567890dadadaddada25___"})
    void shouldValidateValidName(String name) {
        Assertions.assertTrue(FieldValidator.isNameValid(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "3_", "34_%%", "__o1234567890dadadaddada25_long"})
    void shouldValidateInvalidName(String name) {
        Assertions.assertFalse(FieldValidator.isNameValid(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {"gmail@gmail.com", "tlen@o2.pl", "xxx@op.pl", "p.x@wp.pl", "a@w.c", "sub@sub.com.pl",
            "1@gmail.com", "a@gmail.com"})
    void shouldValidateValidEmail(String email) {
        Assertions.assertTrue(FieldValidator.isEmailValid(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a@", "", "@d.com", "123@.com", "almost.com", "ab@almost.", "@gmail.com", ".@dot.com"})
    void shouldValidateInvalidEmail(String email) {
        Assertions.assertFalse(FieldValidator.isEmailValid(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Password12", "Hard2One", "12enougH"})
    void shouldValidateValidPassword(String password) {
        Assertions.assertTrue(FieldValidator.isPasswordValid(password));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "longbutnotstrongone", "Short1", "123455almost", "9877779a"})
    void shouldValidateInvalidPassword(String password) {
        Assertions.assertFalse(FieldValidator.isPasswordValid(password));
    }

    @Test
    void shouldValidateNulls() {
        Assertions.assertFalse(FieldValidator.isNameValid(null));
        Assertions.assertFalse(FieldValidator.isEmailValid(null));
        Assertions.assertFalse(FieldValidator.isPasswordValid(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", ""})
    void shouldValidateNoContentWithNoContent(String string) {
        Assertions.assertFalse(FieldValidator.hasAnyContent(string));
    }

    @Test
    void shouldValidateNoContentWithNull() {
        Assertions.assertFalse(FieldValidator.hasAnyContent(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12", "a", "a244"})
    void shouldValidateNoContentWithContent(String string) {
        Assertions.assertTrue(FieldValidator.hasAnyContent(string));
    }

    @ParameterizedTest
    @MethodSource("withHtmlStrings")
    void shouldValidateHtmlString(String htmlString) {
        Assertions.assertTrue(FieldValidator.hasHtmlCharacters(htmlString));
    }

    static Stream<String> withHtmlStrings() {
        return Stream.of("<video>", """
                        Some text
                        <d>
                        d
                        </d>
                        """, """
                        <script>Malic
                        """,
                "<d>",
                "\nWhite\n<html>daome</html");
    }
}
