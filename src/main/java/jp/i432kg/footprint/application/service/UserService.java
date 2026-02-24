package jp.i432kg.footprint.application.service;

import jakarta.transaction.Transactional;
import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.repository.UserRepository;
import jp.i432kg.footprint.domain.service.UserDomainService;
import jp.i432kg.footprint.domain.value.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDomainService userDomainService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(UserName userName, LoginId loginId, RawPassword rawPassword, Authority authority, BirthDate birthDate) throws Exception {

        if (userDomainService.isExistUser(loginId)) {
            throw new Exception("このユーザー名は既に存在します");
        }

        final User user = User.builder()
                .name(userName)
                .loginId(loginId)
                .hashedPassword(toHashedPassword(rawPassword))
                .birthDate(birthDate)
                .build();

        userRepository.saveUser(user);
    }

    private HashedPassword toHashedPassword(final RawPassword rawPassword) {
        return HashedPassword.of(passwordEncoder.encode(rawPassword.value()));
    }
}