package jp.i432kg.footprint.application.query;

import jp.i432kg.footprint.application.query.model.UserProfileSummary;
import jp.i432kg.footprint.domain.value.UserId;

import java.util.Optional;

/**
 * ユーザーの参照サービス
 */
public interface UserQueryService {

    /**
     * ユーザープロフィールを取得する。
     * ユーザーが存在しない場合は例外をスローします。
     *
     * @param userId 取得対象のユーザ ID
     * @return 指定したユーザのプロフィール情報
     */
    UserProfileSummary getUserProfile(UserId userId);

    /**
     * ユーザープロフィールを検索する。
     *
     * @param userId 取得対象のユーザ ID
     * @return 指定したユーザのプロフィール情報（存在しない場合は empty）
     */
    Optional<UserProfileSummary> findUserProfile(UserId userId);

}
