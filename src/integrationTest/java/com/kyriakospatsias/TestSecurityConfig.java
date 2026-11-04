package com.kyriakospatsias;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Supplies the {@code jwtDecoder} bean under the "test" profile, where
 * {@link com.kyriakospatsias.SecurityConfigurerAdapter#jwtDecoder()} is excluded via
 * {@code @Profile("!test")}. Trusts a fixed HMAC key instead of fetching Cognito's OIDC metadata,
 * so integration tests don't need network access to AWS and can mint their own tokens via
 * {@link #generateToken()}.
 */
@TestConfiguration
public class TestSecurityConfig {

    // HS256 requires a key of at least 256 bits (32 bytes).
    private static final SecretKeySpec SECRET_KEY = new SecretKeySpec(
            "integration-test-only-signing-key-do-not-use-in-prod".getBytes(StandardCharsets.UTF_8),
            "HmacSHA256");

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(SECRET_KEY)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    static String generateToken() {
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject("integration-test-user")
                    .issuer("integration-test")
                    .issueTime(Date.from(Instant.now()))
                    .expirationTime(Date.from(Instant.now().plusSeconds(300)))
                    .build();
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(new MACSigner(SECRET_KEY.getEncoded()));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to sign test JWT", e);
        }
    }
}
