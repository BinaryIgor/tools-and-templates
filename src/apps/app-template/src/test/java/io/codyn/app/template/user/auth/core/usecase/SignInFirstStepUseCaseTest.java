package io.codyn.app.template.user.auth.core.usecase;

import io.codyn.app.template._common.core.exception.InvalidEmailException;
import io.codyn.app.template._common.core.model.UserRole;
import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template._common.test.TestEmailServer;
import io.codyn.app.template.user.auth.core.model.CurrentUserData;
import io.codyn.app.template.user.auth.core.model.SignInFirstStepCommand;
import io.codyn.app.template.user.auth.core.model.SignedInUser;
import io.codyn.app.template.user.auth.core.model.SignedInUserStep;
import io.codyn.app.template.user.auth.test.TestUserAuthRepository;
import io.codyn.app.template.user.auth.test.TestUserRepository;
import io.codyn.app.template.user.common.core.ActivationTokenFactory;
import io.codyn.app.template.user.common.core.ActivationTokens;
import io.codyn.app.template.user.common.core.BcryptPasswordHasher;
import io.codyn.app.template.user.common.test.TestActivationTokenRepository;
import io.codyn.app.template.user.common.test.TestAuthClient;
import io.codyn.app.template.user.common.test.TestUserEmailsProvider;
import io.codyn.app.template.user.common.test.TestUserObjects;
import io.codyn.test.TestClock;
import io.codyn.test.TestTransactions;
import io.codyn.types.Pair;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public class SignInFirstStepUseCaseTest {

    private SignInFirstStepUseCase useCase;
    private TestAuthClient authClient;
    private TestUserRepository userRepository;
    private TestUserAuthRepository userAuthRepository;
    private CreateUserUseCase createUserUseCase;

    @BeforeEach
    void setup() {
        authClient = new TestAuthClient();
        userRepository = new TestUserRepository();
        userAuthRepository = new TestUserAuthRepository();

        var passwordHasher = new BcryptPasswordHasher();
        useCase = new SignInFirstStepUseCase(authClient, userRepository, userAuthRepository, passwordHasher);

        createUserUseCase = new CreateUserUseCase(userRepository, passwordHasher,
                new ActivationTokens(new TestActivationTokenRepository(),
                        new ActivationTokenFactory(new TestClock())),
                TestUserEmailsProvider.sender(new TestEmailServer()),
                new TestTransactions());
    }

    @ParameterizedTest
    @MethodSource("invalidSignInCases")
    void shouldThrowExceptionGivenInvalidSignInCommand(SignInFirstStepCommand command,
                                                       Exception exception) {
        Assertions.assertThatThrownBy(() -> useCase.handle(command))
                .isEqualTo(exception);
    }

    @ParameterizedTest
    @MethodSource("userStateRolesCases")
    void shouldSignInReturningUserDataAndTokens(UserState state, Collection<UserRole> roles) {
        var testCase = prepareSignsInTestCase(state, roles);
        var command = testCase.first();
        var expected = testCase.second();

        Assertions.assertThat(useCase.handle(command))
                .isEqualTo(expected);
    }

    private Pair<SignInFirstStepCommand, SignedInUserStep> prepareSignsInTestCase(UserState state,
                                                                                  Collection<UserRole> roles) {
        var createUserCommand = TestUserObjects.createUserCommand1();

        createUserUseCase.handle(createUserCommand);

        var command = new SignInFirstStepCommand(createUserCommand.email(), createUserCommand.password());

        userRepository.changeUserState(command.email(), state);
        userAuthRepository.addUserRoles(createUserCommand.id(), roles);

        var tokens = authClient.ofUser(createUserCommand.id());

        var response = new SignedInUser(
                new CurrentUserData(createUserCommand.id(), command.email(), createUserCommand.name(),
                        state, roles), tokens);

        return new Pair<>(command, SignedInUserStep.onlyStep(response));
    }

    static Stream<Arguments> invalidSignInCases() {
        return Stream.of(
                Arguments.of(new SignInFirstStepCommand(null, null),
                        new InvalidEmailException(null)));
    }

    static Stream<Arguments> userStateRolesCases() {
        return Stream.of(
                Arguments.of(UserState.CREATED, Set.of()),
                Arguments.of(UserState.ACTIVATED, Set.of()),
                Arguments.of(UserState.ONBOARDED, Set.of()),
                Arguments.of(UserState.ONBOARDED, Set.of(UserRole.ADMIN, UserRole.MODERATOR))
        );
    }
}
