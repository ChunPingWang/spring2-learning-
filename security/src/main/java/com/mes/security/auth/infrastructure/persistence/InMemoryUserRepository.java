package com.mes.security.auth.infrastructure.persistence;

import com.mes.security.auth.domain.model.Role;
import com.mes.security.auth.domain.model.User;
import com.mes.security.auth.domain.model.UserId;
import com.mes.security.auth.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [DDD Pattern: Repository - Adapter (基礎設施層實作)]
 * [SOLID: DIP - 實作領域層定義的 UserRepository 介面]
 * [SOLID: LSP - 可替換為 JPA/MyBatis 等其他實作]
 * [Hexagonal Architecture: Output Adapter]
 *
 * 使用 ConcurrentHashMap 的記憶體實作。
 * 適用於開發、測試和教學環境。
 */
@Component
public class InMemoryUserRepository implements UserRepository {

    private final ConcurrentHashMap<UserId, User> store =
            new ConcurrentHashMap<UserId, User>();

    @Override
    public Optional<User> findById(UserId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<User>(store.values());
    }

    @Override
    public void save(User aggregate) {
        store.put(aggregate.getId(), aggregate);
    }

    @Override
    public void deleteById(UserId id) {
        store.remove(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        for (User user : store.values()) {
            if (user.getUsername().getValue().equals(username)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    @Override
    public List<User> findByRole(String roleName) {
        List<User> result = new ArrayList<User>();
        for (User user : store.values()) {
            for (Role role : user.getRoles()) {
                if (role.getName().equalsIgnoreCase(roleName)) {
                    result.add(user);
                    break;
                }
            }
        }
        return result;
    }
}
