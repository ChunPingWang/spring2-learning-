package com.mes.security.auth.infrastructure.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * [Hexagonal Architecture: Infrastructure Config - JWT 設定]
 * [SOLID: SRP - 只負責 JWT 編碼器和解碼器的 Bean 設定]
 *
 * 使用對稱式金鑰 (HMAC-SHA256) 進行 JWT 的簽名和驗證。
 * 金鑰從 application.yml 的 mes.security.jwt.secret 讀取。
 *
 * 注意：在生產環境中應使用非對稱金鑰 (RSA/EC)。
 */
@Configuration
public class JwtConfig {

    @Value("${mes.security.jwt.secret}")
    private String jwtSecret;

    /**
     * [SOLID: DIP - 提供 JwtDecoder 抽象給 Spring Security]
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(
                jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .build();
    }

    /**
     * [SOLID: DIP - 提供 JwtEncoder 抽象給 JwtTokenProvider]
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        SecretKey secretKey = new SecretKeySpec(
                jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        OctetSequenceKey jwk = new OctetSequenceKey.Builder(secretKey)
                .build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<SecurityContext>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }
}
