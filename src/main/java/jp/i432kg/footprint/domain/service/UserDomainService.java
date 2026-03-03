package jp.i432kg.footprint.domain.service;

import jp.i432kg.footprint.domain.repository.UserRepository;
import jp.i432kg.footprint.domain.value.EmailAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * ユーザーに関するドメインサービス
 */
@Service
@RequiredArgsConstructor
public class UserDomainService {

    private final UserRepository userRepository;

    /**
     * メールアドレスに紐づくユーザーが存在するかを判定します。
     *
     * @param email メールアドレス
     * @return メールアドレスに紐づくユーザーが存在する場合は true、存在しない場合は false
     */
    public boolean isExistUser(final EmailAddress email) {
        return userRepository.countUser(email) > 0;
    }
}
