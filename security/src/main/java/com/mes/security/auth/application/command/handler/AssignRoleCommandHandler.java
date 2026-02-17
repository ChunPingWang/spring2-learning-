package com.mes.security.auth.application.command.handler;

import com.mes.common.cqrs.CommandHandler;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.security.auth.application.command.AssignRoleCommand;
import com.mes.security.auth.domain.model.Role;
import com.mes.security.auth.domain.model.User;
import com.mes.security.auth.domain.model.UserId;
import com.mes.security.auth.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * [CQRS Pattern: Command Handler - 指派角色處理器]
 * [SOLID: SRP - 只負責處理角色指派的業務邏輯]
 * [SOLID: DIP - 依賴 Repository 介面]
 *
 * 處理流程：
 * 1. 載入 User 聚合根
 * 2. 查找預定義角色
 * 3. 呼叫 assignRole
 * 4. 儲存
 */
@Component
public class AssignRoleCommandHandler implements CommandHandler<AssignRoleCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(AssignRoleCommandHandler.class);

    private final UserRepository userRepository;

    private static final Map<String, Role> PREDEFINED_ROLES = new HashMap<String, Role>();

    static {
        PREDEFINED_ROLES.put("ADMIN", Role.ROLE_ADMIN);
        PREDEFINED_ROLES.put("OPERATOR", Role.ROLE_OPERATOR);
        PREDEFINED_ROLES.put("VIEWER", Role.ROLE_VIEWER);
    }

    public AssignRoleCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Void handle(AssignRoleCommand command) {
        log.debug("處理指派角色命令: userId={}, roleName={}", command.getUserId(), command.getRoleName());

        User user = userRepository.findById(UserId.of(command.getUserId()))
                .orElseThrow(new EntityNotFoundSupplier("User", command.getUserId()));

        Role role = PREDEFINED_ROLES.get(command.getRoleName().toUpperCase());
        if (role == null) {
            throw new IllegalArgumentException("Unknown role: " + command.getRoleName());
        }

        user.assignRole(role);
        userRepository.save(user);

        log.info("角色指派成功: userId={}, roleName={}", command.getUserId(), command.getRoleName());
        return null;
    }

    @Override
    public Class<AssignRoleCommand> getCommandType() {
        return AssignRoleCommand.class;
    }

    /**
     * 用於 Optional.orElseThrow 的 Supplier，JDK 8 相容。
     */
    private static class EntityNotFoundSupplier implements java.util.function.Supplier<EntityNotFoundException> {
        private final String entityName;
        private final String id;

        EntityNotFoundSupplier(String entityName, String id) {
            this.entityName = entityName;
            this.id = id;
        }

        @Override
        public EntityNotFoundException get() {
            return new EntityNotFoundException(entityName, id);
        }
    }
}
