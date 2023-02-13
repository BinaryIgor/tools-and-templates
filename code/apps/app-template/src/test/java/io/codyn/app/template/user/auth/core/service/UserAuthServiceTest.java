package io.codyn.app.template.user.auth.core.service;

import io.codyn.app.template._common.core.exception.AppException;
import io.codyn.app.template._common.core.exception.InvalidEmailException;
import io.codyn.app.template._common.core.model.UserRole;
import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template._common.test.TestEmailServer;
import io.codyn.app.template.user.auth.core.model.CurrentUserData;
import io.codyn.app.template.user.auth.core.model.SignedInUser;
import io.codyn.app.template.user.auth.core.model.SignedInUserStep;
import io.codyn.app.template.user.auth.core.model.UserSignInRequest;
import io.codyn.app.template.user.auth.core.usecase.CreateUserUseCase;
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

public class UserAuthServiceTest {

    private UserAuthService service;
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
        service = new UserAuthService(authClient, userRepository, userAuthRepository, passwordHasher);

        createUserUseCase = new CreateUserUseCase(userRepository, passwordHasher,
                new ActivationTokens(new TestActivationTokenRepository(),
                        new ActivationTokenFactory(new TestClock())),
                TestUserEmailsProvider.sender(new TestEmailServer()),
                new TestTransactions());
    }

    @ParameterizedTest
    @MethodSource("invalidSignInCases")
    void shouldThrowExceptionGivenInvalidSignInRequest(UserSignInRequest request,
                                                       AppException exception) {
        Assertions.assertThatThrownBy(() -> service.authenticate(request))
                .isEqualTo(exception);
    }

    @ParameterizedTest
    @MethodSource("userStateRolesCases")
    void shouldAuthenticateReturningUserDataAndTokens(UserState state, Collection<UserRole> roles) {
        var testCase = prepareAuthenticateTestCase(state, roles);
        var request = testCase.first();
        var expected = testCase.second();

        Assertions.assertThat(service.authenticate(request))
                .isEqualTo(expected);
    }

    private Pair<UserSignInRequest, SignedInUserStep> prepareAuthenticateTestCase(UserState state,
                                                                                  Collection<UserRole> roles) {
        var command = TestUserObjects.createUserCommand1();

        createUserUseCase.handle(command);

        var request = new UserSignInRequest(command.email(), command.password());

        userRepository.changeUserState(command.email(), state);
        userAuthRepository.addUserRoles(command.id(), roles);

        var tokens = authClient.ofUser(command.id());

        var response = new SignedInUser(new CurrentUserData(command.id(), command.email(), command.name(),
                state, roles), tokens);

        return new Pair<>(request, SignedInUserStep.onlyStep(response));
    }

    static Stream<Arguments> invalidSignInCases() {
        return Stream.of(
                Arguments.of(new UserSignInRequest(null, null),
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
