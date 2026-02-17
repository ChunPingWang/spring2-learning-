package com.mes.security.auth.application.command.handler;

import com.mes.common.cqrs.CommandHandler;
import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.security.auth.application.command.ChangePasswordCommand;
import com.mes.security.auth.application.port.PasswordEncoderPort;
import com.mes.security.auth.domain.model.User;
import com.mes.security.auth.domain.model.UserId;
import com.mes.security.auth.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Command Handler - 變更密碼處理器]
 * [SOLID: SRP - 只負責處理密碼變更的業務邏輯]
 * [SOLID: DIP - 依賴 Repository 和 PasswordEncoderPort 介面]
 *
 * 處理流程：
 * 1. 載入 User 聚合根
 * 2. 驗證舊密碼
 * 3. 編碼新密碼
 * 4. 呼叫 changePassword
 * 5. 儲存
 */
@Component
public class ChangePasswordCommandHandler implements CommandHandler<ChangePasswordCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(ChangePasswordCommandHandler.class);

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public ChangePasswordCommandHandler(UserRepository userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Void handle(ChangePasswordCommand command) {
        log.debug("處理變更密碼命令: userId={}", command.getUserId());

        User user = userRepository.findById(UserId.of(command.getUserId()))
                .orElseThrow(new java.util.function.Supplier<EntityNotFoundException>() {
                    @Override
                    public EntityNotFoundException get() {
                        return new EntityNotFoundException("User", command.getUserId());
                    }
                });

        // 驗證舊密碼
        if (!passwordEncoder.matches(command.getOldPassword(), user.getEncodedPassword())) {
            throw new BusinessRuleViolationException("Old password does not match");
        }

        // 編碼新密碼並更新
        String newEncodedPassword = passwordEncoder.encode(command.getNewPassword());
        user.changePassword(newEncodedPassword);
        userRepository.save(user);

        log.info("密碼變更成功: userId={}", command.getUserId());
        return null;
    }

    @Override
    public Class<ChangePasswordCommand> getCommandType() {
        return ChangePasswordCommand.class;
    }
}
