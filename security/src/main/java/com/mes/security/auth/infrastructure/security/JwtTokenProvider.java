package com.mes.security.auth.infrastructure.security;

import com.mes.security.auth.domain.model.Role;
import com.mes.security.auth.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * [Hexagonal Architecture: Infrastructure - JWT Token 提供者]
 * [SOLID: SRP - 只負責 JWT Token 的產生]
 * [SOLID: DIP - 依賴 JwtEncoder 介面，不依賴具體實作]
 *
 * 使用 Spring Security OAuth2 的 JwtEncoder 產生 JWT Token。
 * Token 包含以下 Claims:
 * - sub: 使用者名稱
 * - roles: 角色名稱清單
 * - iat: 發行時間
 * - exp: 過期時間
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final JwtEncoder jwtEncoder;
    private final long expirationMs;

    public JwtTokenProvider(JwtEncoder jwtEncoder,
                            @Value("${mes.security.jwt.expiration-ms:3600000}") long expirationMs) {
        this.jwtEncoder = jwtEncoder;
        this.expirationMs = expirationMs;
    }

    /**
     * 根據使用者名稱和角色集合產生 JWT Token。
     *
     * @param username 使用者名稱
     * @param roles    角色名稱集合
     * @return JWT Token 字串
     */
    public String generateToken(String username, Set<String> roles) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);

        List<String> roleList = new ArrayList<String>(roles);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(username)
                .issuedAt(now)
                .expiresAt(expiry)
                .claim("roles", roleList)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        log.debug("JWT Token 已產生: username={}, roles={}", username, roles);
        return token;
    }

    /**
     * 根據領域 User 物件產生 JWT Token。
     *
     * @param user 使用者聚合根
     * @return JWT Token 字串
     */
    public String generateToken(User user) {
        java.util.Set<String> roleNames = new java.util.HashSet<String>();
        for (Role role : user.getRoles()) {
            roleNames.add(role.getName());
        }
        return generateToken(user.getUsername().getValue(), roleNames);
    }
}
