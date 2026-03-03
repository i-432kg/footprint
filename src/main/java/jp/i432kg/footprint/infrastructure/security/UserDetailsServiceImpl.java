package jp.i432kg.footprint.infrastructure.security;

import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.infrastructure.security.mapper.AuthMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * ユーザー認証情報を取得するためのサービス実装クラス
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthMapper authMapper;

    @NonNull
    @Override
    public UserDetailsImpl loadUserByUsername(@NonNull final String username)
            throws UsernameNotFoundException {

        return authMapper.findAuthUserByLoginId(EmailAddress.of(username))
                .map(UserDetailsImpl::fromEntity)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
