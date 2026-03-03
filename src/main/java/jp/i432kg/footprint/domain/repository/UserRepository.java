package jp.i432kg.footprint.domain.repository;

import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.value.EmailAddress;

/**
 * ユーザーに関するリポジトリインターフェース
 */
public interface UserRepository {

    /**
     * ユーザーの存在数をカウントします。
     *
     * @param email メールアドレス
     * @return メールアドレスに紐づくユーザー数
     */
    int countUser(EmailAddress email);

    /**
     * ユーザーを保存します。
     *
     * @param user 保存するユーザー情報
     */
    void saveUser(User user);

}
