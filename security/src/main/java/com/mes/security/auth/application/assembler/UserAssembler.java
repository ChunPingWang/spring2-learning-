package com.mes.security.auth.application.assembler;

import com.mes.security.auth.application.query.dto.UserView;
import com.mes.security.auth.domain.model.Role;
import com.mes.security.auth.domain.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * [DDD Pattern: Assembler - 使用者組裝器]
 * [SOLID: SRP - 只負責 User 聚合根與 UserView DTO 的轉換]
 *
 * 將領域模型 User 轉換為應用層 DTO UserView。
 * 此轉換過程中會過濾敏感資訊（如密碼）。
 */
public final class UserAssembler {

    private UserAssembler() {
        // 工具類別不允許實例化
    }

    /**
     * 將 User 聚合根轉換為 UserView DTO。
     *
     * @param user 使用者聚合根
     * @return UserView DTO
     */
    public static UserView toView(User user) {
        List<String> roleNames = new ArrayList<String>();
        for (Role role : user.getRoles()) {
            roleNames.add(role.getName());
        }
        return new UserView(
                user.getId().getValue(),
                user.getUsername().getValue(),
                user.getEmail().getValue(),
                roleNames,
                user.isEnabled(),
                user.isLocked(),
                user.getCreatedAt()
        );
    }

    /**
     * 將 User 聚合根清單轉換為 UserView DTO 清單。
     *
     * @param users 使用者聚合根清單
     * @return UserView DTO 清單
     */
    public static List<UserView> toViewList(List<User> users) {
        List<UserView> views = new ArrayList<UserView>();
        for (User user : users) {
            views.add(toView(user));
        }
        return views;
    }
}
