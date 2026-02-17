package com.mes.security.auth.application.query.handler;

import com.mes.common.cqrs.QueryHandler;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.security.auth.application.assembler.UserAssembler;
import com.mes.security.auth.application.query.GetUserQuery;
import com.mes.security.auth.application.query.dto.UserView;
import com.mes.security.auth.domain.model.User;
import com.mes.security.auth.domain.model.UserId;
import com.mes.security.auth.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Query Handler - 取得使用者查詢處理器]
 * [SOLID: SRP - 只負責處理單一使用者的查詢]
 * [SOLID: DIP - 依賴 Repository 介面]
 *
 * 從 Repository 載入使用者並轉換為 UserView DTO。
 */
@Component
public class GetUserQueryHandler implements QueryHandler<GetUserQuery, UserView> {

    private static final Logger log = LoggerFactory.getLogger(GetUserQueryHandler.class);

    private final UserRepository userRepository;

    public GetUserQueryHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserView handle(GetUserQuery query) {
        log.debug("處理取得使用者查詢: userId={}", query.getUserId());

        User user = userRepository.findById(UserId.of(query.getUserId()))
                .orElseThrow(new java.util.function.Supplier<EntityNotFoundException>() {
                    @Override
                    public EntityNotFoundException get() {
                        return new EntityNotFoundException("User", query.getUserId());
                    }
                });

        return UserAssembler.toView(user);
    }

    @Override
    public Class<GetUserQuery> getQueryType() {
        return GetUserQuery.class;
    }
}
