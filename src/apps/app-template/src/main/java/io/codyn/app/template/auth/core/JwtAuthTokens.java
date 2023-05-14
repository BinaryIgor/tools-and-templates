package io.codyn.app.template.auth.core;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import io.codyn.app.template._common.core.exception.InvalidAuthTokenException;
import io.codyn.app.template.auth.api.AuthenticatedUser;
import io.codyn.app.template.auth.api.UserAuthData;
import io.codyn.app.template.auth.api.UserAuthDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class JwtAuthTokens implements AuthTokenCreator, AuthTokenAuthenticator {

    private static final String TOKEN_TYPE_CLAIM = "typ";
    private static final Logger log = LoggerFactory.getLogger(JwtAuthTokens.class);
    private final UserAuthDataRepository authDataRepository;
    private final Clock clock;
    private final String issuer;
    private final Algorithm algorithm;
    private final Duration accessTokenDuration;
    private final Duration refreshTokenDuration;

    public JwtAuthTokens(UserAuthDataRepository authDataRepository, Config config) {
        this.authDataRepository = authDataRepository;
        this.clock = config.clock;
        this.issuer = config.issuer;
        this.algorithm = config.algorithm;
        this.accessTokenDuration = config.accessTokenDuration;
        this.refreshTokenDuration = config.refreshTokenDuration;
    }

    @Override
    public AuthTokens ofUser(UUID id) {
        var issuedAt = clock.instant();

        var accessToken = token(id, issuedAt, AuthTokenType.ACCESS);
        var refreshToken = token(id, issuedAt, AuthTokenType.REFRESH);

        return new AuthTokens(accessToken, refreshToken);
    }

    private AuthToken token(UUID id, Instant issuedAt, AuthTokenType type) {
        var tokenDuration = type == AuthTokenType.ACCESS ? accessTokenDuration : refreshTokenDuration;

        var expiresAt = issuedAt.plus(tokenDuration);

        var token = newToken(issuer, id, type, issuedAt, expiresAt, algorithm);

        return new AuthToken(token, expiresAt);
    }

    static String newToken(String issuer,
                           UUID subject,
                           AuthTokenType type,
                           Instant issuedAt,
                           Instant expiresAt,
                           Algorithm algorithm) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(subject.toString())
                .withClaim(TOKEN_TYPE_CLAIM, type.name())
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    @Override
    public AuthTokens refresh(String refreshToken) {
        var user = validateToken(refreshToken, AuthTokenType.REFRESH);
        return ofUser(user.id());
    }

    private AuthenticatedUser validateToken(String token, AuthTokenType type) {
        UUID userId;

        try {
            var decodedToken = tokenVerifier(type).verify(token);
            userId = UUID.fromString(decodedToken.getSubject());
        } catch (Exception e) {
            log.warn("Invalid {} token", type, e);
            throw new InvalidAuthTokenException("Invalid %s token".formatted(type));
        }

        return authDataRepository.ofId(userId)
                .map(UserAuthData::toAuthenticatedUser)
                .orElseThrow(() -> new InvalidAuthTokenException("User %s doesn't exist".formatted(userId)));
    }

    private JWTVerifier tokenVerifier(AuthTokenType type) {
        var builder = JWT.require(algorithm)
                .withIssuer(issuer)
                .withClaim(TOKEN_TYPE_CLAIM, type.name());

        if (builder instanceof JWTVerifier.BaseVerification b) {
            return b.build(clock);
        }

        return builder.build();
    }

    @Override
    public AuthenticatedUser authenticate(String accessToken) {
        return validateToken(accessToken, AuthTokenType.ACCESS);
    }

    /*
    https://crypto.stackexchange.com/questions/53826/hmac-sha256-vs-hmac-sha512-for-jwt-api-authentication
    Both algorithms provide plenty of security, near the output size of the hash.
    So even though HMAC-512 will be stronger, the difference is inconsequential.
    If this ever breaks it is because the algorithm itself is broken and as both hash algorithms are related, it is likely that both would be in trouble.
    However, no such attack is known and the HMAC construct itself appears to be very strong indeed.

    SHA-512 is indeed faster than SHA-256 on 64 bit machines.
    It may be that the overhead provided by the block size of SHA-512 is detrimental to HMAC-ing short length message sizes. But you can speedup larger messages sizes using HMAC-SHA-512 for sure.
    Then again, SHA-256 is plenty fast itself, and is faster on 32 bit and lower machines, so I'd go for HMAC-SHA-256 if lower end machines could be involved.

    Note that newer x86 processors also contain SHA-1 and SHA-256 accelerator hardware, so that may shift the speed advantage back into SHA-256's favor compared to SHA-512.
     */
    public record Config(
            String issuer,
            Algorithm algorithm,
            Duration accessTokenDuration,
            Duration refreshTokenDuration,
            Clock clock) {

        public Config(String issuer,
                      byte[] tokenKey,
                      Duration accessTokenDuration,
                      Duration refreshTokenDuration,
                      Clock clock) {
            this(issuer, Algorithm.HMAC512(tokenKey), accessTokenDuration, refreshTokenDuration, clock);
        }
    }
}
