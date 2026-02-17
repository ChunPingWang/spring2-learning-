package com.mes.security.auth.application.command.handler;

import com.mes.common.cqrs.CommandHandler;
import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.security.auth.application.command.CreateUserCommand;
import com.mes.security.auth.application.port.PasswordEncoderPort;
import com.mes.security.auth.domain.model.Email;
import com.mes.security.auth.domain.model.Role;
import com.mes.security.auth.domain.model.User;
import com.mes.security.auth.domain.model.UserId;
import com.mes.security.auth.domain.model.Username;
import com.mes.security.auth.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [CQRS Pattern: Command Handler - 建立使用者處理器]
 * [SOLID: SRP - 只負責處理建立使用者的業務邏輯]
 * [SOLID: DIP - 依賴 Repository 和 PasswordEncoderPort 介面]
 *
 * 處理流程：
 * 1. 檢查使用者名稱是否重複
 * 2. 編碼密碼
 * 3. 建立 User 聚合根
 * 4. 指派角色
 * 5. 儲存到 Repository
 */
@Component
public class CreateUserCommandHandler implements CommandHandler<CreateUserCommand, String> {

    private static final Logger log = LoggerFactory.getLogger(CreateUserCommandHandler.class);

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;

    private static final Map<String, Role> PREDEFINED_ROLES = new HashMap<String, Role>();

    static {
        PREDEFINED_ROLES.put("ADMIN", Role.ROLE_ADMIN);
        PREDEFINED_ROLES.put("OPERATOR", Role.ROLE_OPERATOR);
        PREDEFINED_ROLES.put("VIEWER", Role.ROLE_VIEWER);
    }

    public CreateUserCommandHandler(UserRepository userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String handle(CreateUserCommand command) {
        log.debug("處理建立使用者命令: username={}", command.getUsername());

        // 1. 檢查使用者名稱是否重複
        if (userRepository.existsByUsername(command.getUsername())) {
            throw new BusinessRuleViolationException(
                    "Username already exists: " + command.getUsername());
        }

        // 2. 編碼密碼
        String encodedPassword = passwordEncoder.encode(command.getPassword());

        // 3. 建立 User 聚合根
        UserId userId = UserId.generate();
        Username username = new Username(command.getUsername());
        Email email = new Email(command.getEmail());
        User user = new User(userId, username, encodedPassword, email);

        // 4. 指派角色
        List<String> roleNames = command.getRoleNames();
        if (roleNames != null) {
            for (String roleName : roleNames) {
                Role role = PREDEFINED_ROLES.get(roleName.toUpperCase());
                if (role != null) {
                    user.assignRole(role);
                }
            }
        }

        // 5. 儲存
        userRepository.save(user);
        log.info("使用者建立成功: userId={}, username={}", userId.getValue(), command.getUsername());

        return userId.getValue();
    }

    @Override
    public Class<CreateUserCommand> getCommandType() {
        return CreateUserCommand.class;
    }
}
