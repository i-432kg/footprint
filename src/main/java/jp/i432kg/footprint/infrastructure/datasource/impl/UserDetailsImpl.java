package jp.i432kg.footprint.infrastructure.datasource.impl;

import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.value.UserId;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final UserId userId;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String password;
    private final String userName;

    private UserDetailsImpl(User user) {
        this.userId = user.getId();
        this.authorities = List.of(new SimpleGrantedAuthority(user.getAuthority().name()));
        this.password = user.getHashedPassword().value();
        this.userName = user.getName().value();
    }

    public static UserDetailsImpl fromDomainUser(User user) {
        return new UserDetailsImpl(user);
    }

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    @NonNull
    public String getPassword() {
        return password;
    }

    @Override
    @NonNull
    public String getUsername() {
        return userName;
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