package com.mes.security.auth.domain.service;

import com.mes.common.ddd.annotation.DomainService;
import com.mes.security.auth.domain.model.User;

/**
 * [DDD Pattern: Domain Service - 認證領域服務]
 * [SOLID: SRP - 只負責跨聚合的認證相關邏輯]
 * [SOLID: DIP - 不依賴任何基礎設施層的實作]
 *
 * AuthenticationDomainService 封裝不屬於單一聚合根的認證領域邏輯。
 * 例如：跨角色的權限檢查、認證規則的集中管理。
 *
 * 注意：此類別不依賴 Spring 或任何框架，是純粹的領域邏輯。
 */
@DomainService
public class AuthenticationDomainService {

    /**
     * 檢查使用者是否擁有對指定資源和動作的權限。
     *
     * @param user     使用者
     * @param resource 資源名稱
     * @param action   動作名稱
     * @return 若擁有該權限且帳號可用則回傳 true
     */
    public boolean hasPermission(User user, String resource, String action) {
        if (user == null) {
            return false;
        }
        if (!user.isEnabled() || user.isLocked()) {
            return false;
        }
        return user.hasPermission(resource, action);
    }
}
