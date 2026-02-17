package com.mes.security.auth.infrastructure.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * [Hexagonal Architecture: Infrastructure 單元測試]
 *
 * 測試 JwtTokenProvider 產生的 JWT Token 是否包含正確的 Claims。
 */
@DisplayName("JwtTokenProvider 測試")
class JwtTokenProviderTest {

    private static final String SECRET = "mySecretKeyForTeachingPurposesMustBeAtLeast256BitsLong!!!";

    private JwtTokenProvider jwtTokenProvider;
    private NimbusJwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        SecretKey secretKey = new SecretKeySpec(
                SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        OctetSequenceKey jwk = new OctetSequenceKey.Builder(secretKey).build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<SecurityContext>(new JWKSet(jwk));
        JwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource);

        jwtTokenProvider = new JwtTokenProvider(jwtEncoder, 3600000L);
        jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    @Test
    @DisplayName("產生的 Token 不應為空")
    void shouldGenerateNonEmptyToken() {
        Set<String> roles = new HashSet<String>();
        roles.add("ADMIN");

        String token = jwtTokenProvider.generateToken("testuser", roles);

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Token 應包含正確的 subject claim")
    void shouldContainCorrectSubject() {
        Set<String> roles = new HashSet<String>();
        roles.add("OPERATOR");

        String token = jwtTokenProvider.generateToken("myuser", roles);
        Jwt jwt = jwtDecoder.decode(token);

        assertThat(jwt.getSubject()).isEqualTo("myuser");
    }

    @Test
    @DisplayName("Token 應包含 roles claim")
    @SuppressWarnings("unchecked")
    void shouldContainRolesClaim() {
        Set<String> roles = new HashSet<String>();
        roles.add("ADMIN");
        roles.add("OPERATOR");

        String token = jwtTokenProvider.generateToken("testuser", roles);
        Jwt jwt = jwtDecoder.decode(token);

        List<String> tokenRoles = jwt.getClaim("roles");
        assertThat(tokenRoles).isNotNull();
        assertThat(tokenRoles).containsExactlyInAnyOrder("ADMIN", "OPERATOR");
    }

    @Test
    @DisplayName("Token 應包含 exp claim")
    void shouldContainExpirationClaim() {
        Set<String> roles = new HashSet<String>();
        roles.add("VIEWER");

        String token = jwtTokenProvider.generateToken("testuser", roles);
        Jwt jwt = jwtDecoder.decode(token);

        assertThat(jwt.getExpiresAt()).isNotNull();
        assertThat(jwt.getIssuedAt()).isNotNull();
        assertThat(jwt.getExpiresAt()).isAfter(jwt.getIssuedAt());
    }
}
