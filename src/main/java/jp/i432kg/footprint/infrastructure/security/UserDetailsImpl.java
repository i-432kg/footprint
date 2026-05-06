package jp.i432kg.footprint.infrastructure.security;

import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.infrastructure.security.mapper.AuthMapper;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security で認証情報を保持するための実装クラス
 */
public class UserDetailsImpl implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final UserId userId;
    private final String email;
    @Getter
    private final String displayUsername;
    private final String password;

    private UserDetailsImpl(final AuthMapper.AuthUserEntity entity) {
        this.userId = entity.getUserId();
        this.email = entity.getEmail();
        this.displayUsername = entity.getDisplayUsername();
        this.password = entity.getPassword();
    }

    public static UserDetailsImpl fromEntity(AuthMapper.AuthUserEntity entity) {
        return new UserDetailsImpl(entity);
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
     * @return Spring Security の認証に使用する識別子（ログインIDのメールアドレス）
     */
    @Override
    @NonNull
    public String getUsername() {
        return email;
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