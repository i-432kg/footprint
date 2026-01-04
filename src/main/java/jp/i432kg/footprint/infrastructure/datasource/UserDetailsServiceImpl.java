package jp.i432kg.footprint.infrastructure.datasource;

import jakarta.transaction.Transactional;
import jp.i432kg.footprint.domain.model.user.User;
import jp.i432kg.footprint.domain.repository.UserMapper;
import jp.i432kg.footprint.domain.value.Authority;
import jp.i432kg.footprint.domain.value.HashedPassword;
import jp.i432kg.footprint.domain.value.RawPassword;
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

    @NonNull
    @Override
    public UserDetailsImpl loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        User user = userMapper.findByName(new UserName(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new UserDetailsImpl(user);
    }

    @Transactional
    public void register(UserName username, RawPassword rawPassword, Authority authority) {
        userMapper.insert(username, toHashedPassword(rawPassword), authority);
    }

    public boolean isExistUser(String username) {
        return userMapper.countByName(username) > 0;
    }

    private HashedPassword toHashedPassword(final RawPassword rawPassword) {
        return new HashedPassword(passwordEncoder.encode(rawPassword.value()));
    }
}
