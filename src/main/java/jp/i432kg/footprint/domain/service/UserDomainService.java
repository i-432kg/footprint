package jp.i432kg.footprint.domain.service;

import jp.i432kg.footprint.domain.repository.UserRepository;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.UserId;
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
     * 対象のユーザが存在するかどうか判定します。
     *
     * @param userId ユーザー ID
     * @return 存在する場合は true / 存在しない場合は false
     */
    public boolean isExistUser(final UserId userId) {
        return userRepository.existsById(userId);
    }

    /**
     * 指定されたメールアドレスが既に登録済み（重複）かどうかを判定します。
     *
     * @param email メールアドレス
     * @return 登録済みの場合は true / 未登録の場合は false
     */
    public boolean isEmailAlreadyUsed(final EmailAddress email) {
        return userRepository.existsByEmail(email);
    }
}
