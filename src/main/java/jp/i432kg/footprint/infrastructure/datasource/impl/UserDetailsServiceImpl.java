package jp.i432kg.footprint.infrastructure.datasource.impl;

import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.value.LoginId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.UserMapper;
import jp.i432kg.footprint.domain.value.UserName;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    /**
     * Spring Security でログイン処理を行うために必要
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @NonNull
    @Override
    public UserDetailsImpl loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        User user = userMapper.findByLoginId(LoginId.of(username))
                .orElseThrow(() -> {
                    System.err.println("Login failed: User not found for email [" + username + "]");
                    return new UsernameNotFoundException("User not found: " + username);
                });

        return UserDetailsImpl.fromDomainUser(user);
    }
}
