package com.mes.security.auth.application.port;

/**
 * [Hexagonal Architecture: Output Port - 密碼編碼埠]
 * [SOLID: DIP - 應用層定義介面，基礎設施層實作]
 * [SOLID: ISP - 只定義密碼編碼相關的最小操作]
 *
 * 密碼編碼的出站埠。應用層透過此介面編碼和驗證密碼，
 * 而不需要知道底層使用的具體編碼演算法（如 BCrypt、Argon2 等）。
 */
public interface PasswordEncoderPort {

    /**
     * 將原始密碼編碼。
     *
     * @param rawPassword 原始密碼
     * @return 編碼後的密碼
     */
    String encode(String rawPassword);

    /**
     * 驗證原始密碼是否與編碼後的密碼匹配。
     *
     * @param rawPassword     原始密碼
     * @param encodedPassword 編碼後的密碼
     * @return 若匹配則回傳 true
     */
    boolean matches(String rawPassword, String encodedPassword);
}
