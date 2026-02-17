package com.mes.security.auth.application.command.handler;

import com.mes.common.cqrs.CommandHandler;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.security.auth.application.command.LockUserCommand;
import com.mes.security.auth.domain.model.User;
import com.mes.security.auth.domain.model.UserId;
import com.mes.security.auth.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Command Handler - 鎖定使用者處理器]
 * [SOLID: SRP - 只負責處理使用者鎖定的業務邏輯]
 * [SOLID: DIP - 依賴 Repository 介面]
 *
 * 處理流程：
 * 1. 載入 User 聚合根
 * 2. 呼叫 lock
 * 3. 儲存
 */
@Component
public class LockUserCommandHandler implements CommandHandler<LockUserCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(LockUserCommandHandler.class);

    private final UserRepository userRepository;

    public LockUserCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Void handle(LockUserCommand command) {
        log.debug("處理鎖定使用者命令: userId={}, reason={}", command.getUserId(), command.getReason());

        User user = userRepository.findById(UserId.of(command.getUserId()))
                .orElseThrow(new java.util.function.Supplier<EntityNotFoundException>() {
                    @Override
                    public EntityNotFoundException get() {
                        return new EntityNotFoundException("User", command.getUserId());
                    }
                });

        user.lock(command.getReason());
        userRepository.save(user);

        log.info("使用者鎖定成功: userId={}, reason={}", command.getUserId(), command.getReason());
        return null;
    }

    @Override
    public Class<LockUserCommand> getCommandType() {
        return LockUserCommand.class;
    }
}
