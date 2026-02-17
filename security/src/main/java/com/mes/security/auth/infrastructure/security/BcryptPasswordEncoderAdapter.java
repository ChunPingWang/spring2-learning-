package com.mes.security.auth.infrastructure.security;

import com.mes.security.auth.application.port.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * [Hexagonal Architecture: Output Adapter - BCrypt 密碼編碼器]
 * [SOLID: DIP - 實作應用層定義的 PasswordEncoderPort 介面]
 * [SOLID: SRP - 只負責密碼編碼與驗證]
 *
 * 使用 BCrypt 演算法實作密碼編碼。
 * 透過封裝 Spring Security 的 BCryptPasswordEncoder，
 * 讓應用層不直接依賴 Spring Security。
 */
@Component
public class BcryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public BcryptPasswordEncoderAdapter(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public String encode(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
