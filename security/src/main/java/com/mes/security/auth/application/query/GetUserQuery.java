package com.mes.security.auth.application.query;

import com.mes.common.cqrs.Query;
import com.mes.security.auth.application.query.dto.UserView;

/**
 * [CQRS Pattern: Query - 取得使用者查詢]
 * [SOLID: SRP - 只攜帶查詢使用者所需的參數]
 *
 * 根據使用者 ID 查詢使用者資訊。
 */
public class GetUserQuery implements Query<UserView> {

    private final String userId;

    public GetUserQuery(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
