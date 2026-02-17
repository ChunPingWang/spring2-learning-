package com.mes.security.auth.application.query.handler;

import com.mes.common.cqrs.QueryHandler;
import com.mes.security.auth.application.assembler.UserAssembler;
import com.mes.security.auth.application.query.ListUsersByRoleQuery;
import com.mes.security.auth.application.query.dto.UserView;
import com.mes.security.auth.domain.model.User;
import com.mes.security.auth.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * [CQRS Pattern: Query Handler - 依角色列出使用者查詢處理器]
 * [SOLID: SRP - 只負責處理依角色列出使用者的查詢]
 * [SOLID: DIP - 依賴 Repository 介面]
 *
 * 從 Repository 查找擁有指定角色的使用者並轉換為 UserView 清單。
 */
@Component
public class ListUsersByRoleQueryHandler implements QueryHandler<ListUsersByRoleQuery, List<UserView>> {

    private static final Logger log = LoggerFactory.getLogger(ListUsersByRoleQueryHandler.class);

    private final UserRepository userRepository;

    public ListUsersByRoleQueryHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserView> handle(ListUsersByRoleQuery query) {
        log.debug("處理依角色列出使用者查詢: roleName={}", query.getRoleName());

        List<User> users = userRepository.findByRole(query.getRoleName());
        return UserAssembler.toViewList(users);
    }

    @Override
    public Class<ListUsersByRoleQuery> getQueryType() {
        return ListUsersByRoleQuery.class;
    }
}
