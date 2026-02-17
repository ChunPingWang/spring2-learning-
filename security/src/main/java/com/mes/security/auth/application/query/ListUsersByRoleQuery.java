package com.mes.security.auth.application.query;

import com.mes.common.cqrs.Query;
import com.mes.security.auth.application.query.dto.UserView;

import java.util.List;

/**
 * [CQRS Pattern: Query - 依角色列出使用者查詢]
 * [SOLID: SRP - 只攜帶依角色查詢使用者所需的參數]
 *
 * 根據角色名稱查詢所有擁有該角色的使用者。
 */
public class ListUsersByRoleQuery implements Query<List<UserView>> {

    private final String roleName;

    public ListUsersByRoleQuery(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
