package com.mes.security.auth.domain.repository;

import com.mes.common.ddd.repository.Repository;
import com.mes.security.auth.domain.model.User;
import com.mes.security.auth.domain.model.UserId;

import java.util.List;
import java.util.Optional;

/**
 * [DDD Pattern: Repository - Port (出站埠)]
 * [SOLID: ISP - 只定義 User 聚合根的存取操作]
 * [SOLID: DIP - 領域層定義介面，基礎設施層實作]
 * [Hexagonal Architecture: 這是一個 Output Port]
 *
 * UserRepository 提供使用者聚合根的持久化介面。
 * 除了基本的 CRUD 外，還提供依使用者名稱和角色查找的方法。
 */
public interface UserRepository extends Repository<User, UserId> {

    /**
     * 根據使用者名稱查找使用者。
     *
     * @param username 使用者名稱
     * @return 使用者的 Optional
     */
    Optional<User> findByUsername(String username);

    /**
     * 檢查使用者名稱是否已存在。
     *
     * @param username 使用者名稱
     * @return 若已存在則回傳 true
     */
    boolean existsByUsername(String username);

    /**
     * 根據角色名稱查找所有擁有該角色的使用者。
     *
     * @param roleName 角色名稱
     * @return 符合條件的使用者清單
     */
    List<User> findByRole(String roleName);
}
