package com.mes.security.auth.adapter.in.web;

import com.mes.common.cqrs.QueryBus;
import com.mes.security.auth.application.query.GetUserQuery;
import com.mes.security.auth.application.query.ListUsersByRoleQuery;
import com.mes.security.auth.application.query.dto.UserView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * [Hexagonal Architecture: Inbound Adapter - 使用者查詢控制器]
 * [CQRS Pattern: Query Side 控制器]
 * [SOLID: SRP - 只負責處理使用者相關的讀取 HTTP 請求]
 *
 * 受保護端點（需要認證，ADMIN 或 OPERATOR 可存取）：
 * - GET /api/v1/users/{id}     - 查詢使用者詳情
 * - GET /api/v1/users?role=    - 依角色列出使用者
 */
@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
public class UserQueryController {

    private static final Logger log = LoggerFactory.getLogger(UserQueryController.class);

    private final QueryBus queryBus;

    public UserQueryController(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    /**
     * 查詢使用者詳情。
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserView>> getUser(@PathVariable String id) {
        log.debug("處理查詢使用者請求: userId={}", id);

        UserView userView = queryBus.dispatch(new GetUserQuery(id));
        return ResponseEntity.ok(ApiResponse.success(userView));
    }

    /**
     * 依角色列出使用者。
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserView>>> listUsersByRole(
            @RequestParam(name = "role") String roleName) {
        log.debug("處理依角色列出使用者請求: roleName={}", roleName);

        List<UserView> users = queryBus.dispatch(new ListUsersByRoleQuery(roleName));
        return ResponseEntity.ok(ApiResponse.success(users));
    }
}
