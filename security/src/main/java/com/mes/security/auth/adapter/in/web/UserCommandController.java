package com.mes.security.auth.adapter.in.web;

import com.mes.common.cqrs.CommandBus;
import com.mes.security.auth.application.command.AssignRoleCommand;
import com.mes.security.auth.application.command.ChangePasswordCommand;
import com.mes.security.auth.application.command.LockUserCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * [Hexagonal Architecture: Inbound Adapter - 使用者命令控制器]
 * [CQRS Pattern: Command Side 控制器]
 * [SOLID: SRP - 只負責處理使用者相關的寫入 HTTP 請求]
 *
 * 受保護端點（需要認證）：
 * - PUT /api/v1/users/{id}/role     - 指派角色（僅 ADMIN）
 * - PUT /api/v1/users/{id}/password - 變更密碼
 * - PUT /api/v1/users/{id}/lock     - 鎖定使用者（僅 ADMIN）
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserCommandController {

    private static final Logger log = LoggerFactory.getLogger(UserCommandController.class);

    private final CommandBus commandBus;

    public UserCommandController(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    /**
     * 指派角色給使用者。僅 ADMIN 可操作。
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> assignRole(
            @PathVariable String id,
            @Valid @RequestBody AssignRoleCommand command) {
        log.debug("處理指派角色請求: userId={}, roleName={}", id, command.getRoleName());

        command.setUserId(id);
        commandBus.dispatch(command);

        return ResponseEntity.ok(ApiResponse.<Void>success("角色指派成功", null));
    }

    /**
     * 變更使用者密碼。
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable String id,
            @Valid @RequestBody ChangePasswordCommand command) {
        log.debug("處理變更密碼請求: userId={}", id);

        command.setUserId(id);
        commandBus.dispatch(command);

        return ResponseEntity.ok(ApiResponse.<Void>success("密碼變更成功", null));
    }

    /**
     * 鎖定使用者帳號。僅 ADMIN 可操作。
     */
    @PutMapping("/{id}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> lockUser(
            @PathVariable String id,
            @Valid @RequestBody LockUserCommand command) {
        log.debug("處理鎖定使用者請求: userId={}, reason={}", id, command.getReason());

        command.setUserId(id);
        commandBus.dispatch(command);

        return ResponseEntity.ok(ApiResponse.<Void>success("使用者已鎖定", null));
    }
}
