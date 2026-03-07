package jp.i432kg.footprint.domain.repository;

import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.UserId;

import java.util.Optional;

/**
 * ユーザーに関するリポジトリインターフェース
 */
public interface UserRepository {


    /**
     * 指定のユーザーIDを持つ有効なユーザーが存在するかを確認します。
     *
     * @param userId ユーザー ID
     * @return 存在する場合は true / 存在しない場合は false
     */
    boolean existsById(UserId userId);

    /**
     * 指定のメールアドレスを持つユーザーが既に登録されているかを確認します。
     *
     * @param email メールアドレス
     * @return 登録済みの場合は true / 未登録の場合は false
     */
    boolean existsByEmail(EmailAddress email);

    /**
     * ユーザーを保存します。
     *
     * @param user 保存するユーザー情報
     */
    void saveUser(User user);

}
