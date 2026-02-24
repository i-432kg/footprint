package jp.i432kg.footprint.infrastructure.datasource.impl;

import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.value.UserId;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final UserId userId;
    private final String password;
    private final String loginId;

    private UserDetailsImpl(User user) {
        this.userId = user.getId();
        this.password = user.getHashedPassword().value();
        this.loginId = user.getLoginId().value();
    }

    public static UserDetailsImpl fromDomainUser(User user) {
        return new UserDetailsImpl(user);
    }

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 現状、ロールベースの権限管理を行っていないため空のリストを返す
        return Collections.emptyList();
    }

    @Override
    @NonNull
    public String getPassword() {
        return password;
    }

    /**
     *
     * @return Spring Security の認証に使用する識別子（ドメインのUserNameとは異なる）
     */
    @Override
    @NonNull
    public String getUsername() {
        return loginId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}