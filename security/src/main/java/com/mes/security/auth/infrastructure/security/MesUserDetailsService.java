package com.mes.security.auth.infrastructure.security;

import com.mes.security.auth.domain.model.Permission;
import com.mes.security.auth.domain.model.Role;
import com.mes.security.auth.domain.model.User;
import com.mes.security.auth.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * [Hexagonal Architecture: Output Adapter - Spring Security UserDetailsService]
 * [SOLID: DIP - 實作 Spring Security 的 UserDetailsService 介面]
 * [SOLID: SRP - 只負責從領域模型載入使用者認證資訊]
 *
 * 從 UserRepository 載入領域 User 並轉換為 Spring Security 的 UserDetails。
 * 角色映射為 ROLE_ 前綴的 GrantedAuthority，
 * 權限映射為 resource:action 格式的 GrantedAuthority。
 */
@Service
public class MesUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(MesUserDetailsService.class);

    private final UserRepository userRepository;

    public MesUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("載入使用者: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(new java.util.function.Supplier<UsernameNotFoundException>() {
                    @Override
                    public UsernameNotFoundException get() {
                        return new UsernameNotFoundException("User not found: " + username);
                    }
                });

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        // 角色 -> ROLE_XXX
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            // 權限 -> resource:action
            for (Permission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(
                        permission.getResource() + ":" + permission.getAction()));
            }
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername().getValue(),
                user.getEncodedPassword(),
                user.isEnabled(),
                true,  // accountNonExpired
                true,  // credentialsNonExpired
                !user.isLocked(),  // accountNonLocked
                authorities
        );
    }
}
