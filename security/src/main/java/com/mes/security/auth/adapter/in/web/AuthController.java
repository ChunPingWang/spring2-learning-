package com.mes.security.auth.adapter.in.web;

import com.mes.common.cqrs.CommandBus;
import com.mes.security.auth.application.command.CreateUserCommand;
import com.mes.security.auth.infrastructure.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

/**
 * [Hexagonal Architecture: Inbound Adapter - 認證控制器]
 * [SOLID: SRP - 只負責處理認證相關的 HTTP 請求（登入、註冊）]
 *
 * 公開端點（不需認證）：
 * - POST /api/v1/auth/login  - 使用者登入，回傳 JWT Token
 * - POST /api/v1/auth/register - 使用者註冊
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final CommandBus commandBus;
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(CommandBus commandBus,
                          UserDetailsService userDetailsService,
                          BCryptPasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtTokenProvider) {
        this.commandBus = commandBus;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 使用者登入。
     * 驗證使用者名稱和密碼，成功後回傳 JWT Token。
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest request) {
        log.debug("處理登入請求: username={}", request.getUsername());

        // 載入使用者
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        // 驗證密碼
        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<String>error(401, "Invalid username or password"));
        }

        // 檢查帳號狀態
        if (!userDetails.isAccountNonLocked()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<String>error(401, "Account is locked"));
        }

        // 提取角色名稱（移除 ROLE_ 前綴）
        Set<String> roles = new HashSet<String>();
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            String auth = authority.getAuthority();
            if (auth.startsWith("ROLE_")) {
                roles.add(auth.substring(5));
            }
        }

        // 產生 JWT Token
        String token = jwtTokenProvider.generateToken(userDetails.getUsername(), roles);

        log.info("使用者登入成功: username={}", request.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Login successful", token));
    }

    /**
     * 使用者註冊。
     * 建立新使用者帳號。
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody CreateUserCommand command) {
        log.debug("處理註冊請求: username={}", command.getUsername());

        String userId = commandBus.dispatch(command);

        log.info("使用者註冊成功: userId={}, username={}", userId, command.getUsername());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(userId));
    }
}
