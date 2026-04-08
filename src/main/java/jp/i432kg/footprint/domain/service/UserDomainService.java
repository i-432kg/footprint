package jp.i432kg.footprint.domain.service;

import jp.i432kg.footprint.domain.exception.EmailAlreadyUsedException;
import jp.i432kg.footprint.domain.repository.UserRepository;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.UserId;
import lombok.RequiredArgsConstructor;

/**
 * ユーザーに関するドメインサービス
 */
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
     * 指定されたメールアドレスが既に登録済みかどうかを判定します。
     *
     * @param email 登録済みか確認するメールアドレス
     * @throws EmailAlreadyUsedException 指定のメールアドレスが使用済みだった場合の例外
     */
    public void ensureEmailNotAlreadyUsed(final EmailAddress email)
            throws EmailAlreadyUsedException{

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException(email);
        }
    }
}
