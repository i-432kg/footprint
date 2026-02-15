package jp.i432kg.footprint.domain.service;

import jp.i432kg.footprint.domain.repository.UserRepository;
import jp.i432kg.footprint.domain.value.LoginId;
import jp.i432kg.footprint.domain.value.UserName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDomainService {

    private final UserRepository userRepository;

    public boolean isExistUser(final LoginId loginId) {
        return userRepository.countUser(loginId) > 0;
    }
}
